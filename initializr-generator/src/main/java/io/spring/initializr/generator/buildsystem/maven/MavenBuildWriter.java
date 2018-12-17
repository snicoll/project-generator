/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.buildsystem.maven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyComparator;
import io.spring.initializr.generator.buildsystem.DependencyContainer;
import io.spring.initializr.generator.buildsystem.DependencyType;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Configuration;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Execution;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Setting;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.util.VersionReference;

/**
 * A {@link MavenBuild} writer.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class MavenBuildWriter {

	public void writeTo(IndentingWriter writer, MavenBuild build) throws IOException {
		writeProject(writer, () -> {
			writeParent(writer, build);
			writeProjectCoordinates(writer, build);
			writePackaging(writer, build);
			writeProjectName(writer, build);
			writeProperties(writer, build);
			writeDependencies(writer, build);
			writeDependencyManagement(writer, build);
			writeBuild(writer, build);
			writeRepositories(writer, build);
		});

	}

	private void writeProject(IndentingWriter writer, Runnable whenWritten) {
		writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.println(
				"<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		writer.indented(() -> {
			writer.println(
					"xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">");
			writeSingleElement(writer, "modelVersion", "4.0.0");
			whenWritten.run();
		});
		writer.println();
		writer.println("</project>");
	}

	private void writeParent(IndentingWriter writer, MavenBuild build) {
		Parent parent = build.getParent();
		if (parent == null) {
			return;
		}
		writer.println("<parent>");
		writer.indented(() -> {
			writeSingleElement(writer, "groupId", parent.getGroupId());
			writeSingleElement(writer, "artifactId", parent.getArtifactId());
			writeSingleElement(writer, "version", parent.getVersion());
			writer.println("<relativePath/> <!-- lookup parent from repository -->");
		});
		writer.println("</parent>");
	}

	private void writeProjectCoordinates(IndentingWriter writer, MavenBuild build) {
		writeSingleElement(writer, "groupId", build.getGroup());
		writeSingleElement(writer, "artifactId", build.getArtifact());
		writeSingleElement(writer, "version", "0.0.1-SNAPSHOT");
	}

	private void writePackaging(IndentingWriter writer, MavenBuild build) {
		String packaging = build.getPackaging();
		if (!"jar".equals(packaging)) {
			writeSingleElement(writer, "packaging", packaging);
		}
	}

	private void writeProjectName(IndentingWriter writer, MavenBuild build) {
		writeSingleElement(writer, "name", build.getName());
		writeSingleElement(writer, "description", build.getDescription());
	}

	private void writeProperties(IndentingWriter writer, MavenBuild build) {
		if (build.getProperties().isEmpty() && build.getVersionProperties().isEmpty()) {
			return;
		}
		writer.println();
		writeElement(writer, "properties", () -> {
			build.getProperties()
					.forEach((key, value) -> writeSingleElement(writer, key, value));
			build.getVersionProperties().forEach((key,
					value) -> writeSingleElement(writer, key.toStandardFormat(), value));
		});
	}

	private void writeDependencies(IndentingWriter writer, MavenBuild build) {
		if (build.dependencies().isEmpty()) {
			return;
		}
		DependencyContainer dependencies = build.dependencies();
		writer.println();
		writeElement(writer, "dependencies", () -> {
			Collection<Dependency> compiledDependencies = writeDependencies(writer,
					dependencies, DependencyType.COMPILE);
			if (!compiledDependencies.isEmpty()) {
				writer.println();
			}
			writeDependencies(writer, dependencies, DependencyType.RUNTIME);
			writeDependencies(writer, dependencies, DependencyType.ANNOTATION_PROCESSOR);
			writeDependencies(writer, dependencies, DependencyType.PROVIDED_RUNTIME);
			writeDependencies(writer, dependencies, DependencyType.TEST_COMPILE,
					DependencyType.TEST_RUNTIME);
		});
	}

	private Collection<Dependency> writeDependencies(IndentingWriter writer,
			DependencyContainer dependencies, DependencyType... types) {
		Collection<Dependency> candidates = filterDependencies(dependencies, types);
		writeCollection(writer, candidates, this::writeDependency);
		return candidates;
	}

	private void writeDependency(IndentingWriter writer, Dependency dependency) {
		writeElement(writer, "dependency", () -> {
			writeSingleElement(writer, "groupId", dependency.getGroupId());
			writeSingleElement(writer, "artifactId", dependency.getArtifactId());
			writeSingleElement(writer, "version",
					determineVersion(dependency.getVersion()));
			writeSingleElement(writer, "scope", scopeForType(dependency.getType()));
			if (isOptional(dependency.getType())) {
				writeSingleElement(writer, "optional", Boolean.toString(true));
			}
		});
	}

	private static Collection<Dependency> filterDependencies(
			DependencyContainer dependencies, DependencyType... types) {
		List<DependencyType> candidates = Arrays.asList(types);
		return dependencies.values().filter((dep) -> candidates.contains(dep.getType()))
				.sorted(DependencyComparator.INSTANCE).collect(Collectors.toList());
	}

	private String scopeForType(DependencyType type) {
		switch (type) {
		case ANNOTATION_PROCESSOR:
			return null;
		case COMPILE:
			return null;
		case PROVIDED_RUNTIME:
			return "provided";
		case RUNTIME:
			return "runtime";
		case TEST_COMPILE:
			return "test";
		case TEST_RUNTIME:
			return "test";
		default:
			throw new IllegalStateException(
					"Unrecognized dependency type '" + type + "'");
		}
	}

	private boolean isOptional(DependencyType type) {
		return type == DependencyType.ANNOTATION_PROCESSOR;
	}

	private void writeDependencyManagement(IndentingWriter writer, MavenBuild build) {
		List<BillOfMaterials> boms = new ArrayList<>(build.getBoms());
		if (boms.isEmpty()) {
			return;
		}
		boms.sort(Comparator.comparing(BillOfMaterials::getOrder));
		writer.println();
		writeElement(writer, "dependencyManagement", () -> writeElement(writer,
				"dependencies", () -> writeCollection(writer, boms, this::writeBom)));
	}

	private void writeBom(IndentingWriter writer, BillOfMaterials bom) {
		writeElement(writer, "dependency", () -> {
			writeSingleElement(writer, "groupId", bom.getGroupId());
			writeSingleElement(writer, "artifactId", bom.getArtifactId());
			writeSingleElement(writer, "version", determineVersion(bom.getVersion()));
			writeSingleElement(writer, "type", "pom");
			writeSingleElement(writer, "scope", "import");
		});
	}

	private String determineVersion(VersionReference versionReference) {
		if (versionReference == null) {
			return null;
		}
		return (versionReference.isProperty())
				? "${" + versionReference.getProperty().toStandardFormat() + "}"
				: versionReference.getValue();
	}

	private void writeBuild(IndentingWriter writer, MavenBuild build) {
		if (build.getSourceDirectory() == null && build.getTestSourceDirectory() == null
				&& build.getPlugins().isEmpty()) {
			return;
		}
		writer.println();
		writeElement(writer, "build", () -> {
			writeSingleElement(writer, "sourceDirectory", build.getSourceDirectory());
			writeSingleElement(writer, "testSourceDirectory",
					build.getTestSourceDirectory());
			writePlugins(writer, build);

		});
	}

	private void writePlugins(IndentingWriter writer, MavenBuild build) {
		if (build.getPlugins().isEmpty()) {
			return;
		}
		writeElement(writer, "plugins",
				() -> writeCollection(writer, build.getPlugins(), this::writePlugin));
	}

	private void writePlugin(IndentingWriter writer, MavenPlugin plugin) {
		writeElement(writer, "plugin", () -> {
			writeSingleElement(writer, "groupId", plugin.getGroupId());
			writeSingleElement(writer, "artifactId", plugin.getArtifactId());
			writeSingleElement(writer, "version", plugin.getVersion());
			writePluginConfiguration(writer, plugin.getConfiguration());
			if (!plugin.getExecutions().isEmpty()) {
				writeElement(writer, "executions", () -> writeCollection(writer,
						plugin.getExecutions(), this::writePluginExecution));
			}
			if (!plugin.getDependencies().isEmpty()) {
				writeElement(writer, "dependencies", () -> writeCollection(writer,
						plugin.getDependencies(), this::writePluginDependency));
			}
		});
	}

	private void writePluginConfiguration(IndentingWriter writer,
			Configuration configuration) {
		if (configuration == null || configuration.getSettings().isEmpty()) {
			return;
		}
		writeElement(writer, "configuration", () -> {
			writeCollection(writer, configuration.getSettings(), this::writeSetting);
		});
	}

	@SuppressWarnings("unchecked")
	private void writeSetting(IndentingWriter writer, Setting setting) {
		if (setting.getValue() instanceof String) {
			writeSingleElement(writer, setting.getName(), (String) setting.getValue());
		}
		else if (setting.getValue() instanceof List) {
			writeElement(writer, setting.getName(), () -> {
				writeCollection(writer, (List<Setting>) setting.getValue(),
						this::writeSetting);
			});
		}
	}

	private void writePluginExecution(IndentingWriter writer, Execution execution) {
		writeElement(writer, "execution", () -> {
			writeSingleElement(writer, "id", execution.getId());
			writeSingleElement(writer, "phase", execution.getPhase());
			List<String> goals = execution.getGoals();
			if (!goals.isEmpty()) {
				writeElement(writer, "goals", () -> goals
						.forEach((goal) -> writeSingleElement(writer, "goal", goal)));
			}
			writePluginConfiguration(writer, execution.getConfiguration());
		});
	}

	private void writePluginDependency(IndentingWriter writer,
			MavenPlugin.Dependency dependency) {
		writeElement(writer, "dependency", () -> {
			writeSingleElement(writer, "groupId", dependency.getGroupId());
			writeSingleElement(writer, "artifactId", dependency.getArtifactId());
			writeSingleElement(writer, "version", dependency.getVersion());
		});
	}

	private void writeRepositories(IndentingWriter writer, MavenBuild build) {
		List<MavenRepository> repositories = filterRepositories(build.getRepositories());
		List<MavenRepository> pluginRepositories = filterRepositories(
				build.getPluginRepositories());
		if (repositories.isEmpty() && pluginRepositories.isEmpty()) {
			return;
		}
		writer.println();
		if (!repositories.isEmpty()) {
			writeRepositories(writer, "repositories", "repository", repositories);
		}
		if (!pluginRepositories.isEmpty()) {
			writeRepositories(writer, "pluginRepositories", "pluginRepository",
					pluginRepositories);
		}
	}

	private List<MavenRepository> filterRepositories(List<MavenRepository> repositories) {
		return repositories.stream()
				.filter((repository) -> !MavenRepository.MAVEN_CENTRAL.equals(repository))
				.collect(Collectors.toList());
	}

	private void writeRepositories(IndentingWriter writer, String containerName,
			String childName, List<MavenRepository> repositories) {
		writeElement(writer, containerName, () -> {
			repositories.forEach((repository) -> {
				writeElement(writer, childName, () -> {
					writeSingleElement(writer, "id", repository.getId());
					writeSingleElement(writer, "name", repository.getName());
					writeSingleElement(writer, "url", repository.getUrl());
					if (repository.isSnapshotsEnabled()) {
						writeElement(writer, "snapshots", () -> writeSingleElement(writer,
								"enabled", Boolean.toString(true)));
					}
				});
			});
		});
	}

	private void writeSingleElement(IndentingWriter writer, String name, String text) {
		if (text != null) {
			writer.print(String.format("<%s>", name));
			writer.print(text);
			writer.println(String.format("</%s>", name));
		}
	}

	private void writeElement(IndentingWriter writer, String name, Runnable withContent) {
		writer.println(String.format("<%s>", name));
		writer.indented(withContent);
		writer.println(String.format("</%s>", name));
	}

	private <T> void writeCollection(IndentingWriter writer, Collection<T> collection,
			BiConsumer<IndentingWriter, T> itemWriter) {
		if (!collection.isEmpty()) {
			collection.forEach((item) -> itemWriter.accept(writer, item));
		}
	}

}
