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

import java.io.StringWriter;
import java.util.function.Consumer;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.DependencyType;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.test.assertj.NodeAssert;
import io.spring.initializr.generator.util.VersionProperty;
import io.spring.initializr.generator.util.VersionReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MavenBuildWriter}.
 *
 * @author Stephane Nicoll
 */
class MavenBuildWriterTests {

	@Test
	void basicPom() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/modelVersion").isEqualTo("4.0.0");
			assertThat(pom).textAtPath("/project/groupId").isEqualTo("com.example.demo");
			assertThat(pom).textAtPath("/project/artifactId").isEqualTo("demo");
			assertThat(pom).textAtPath("/project/version").isEqualTo("0.0.1-SNAPSHOT");
		});
	}

	@Test
	void pomWithNameAndDescription() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.setName("demo project");
		build.setDescription("A demo project");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/modelVersion").isEqualTo("4.0.0");
			assertThat(pom).textAtPath("/project/groupId").isEqualTo("com.example.demo");
			assertThat(pom).textAtPath("/project/artifactId").isEqualTo("demo");
			assertThat(pom).textAtPath("/project/version").isEqualTo("0.0.1-SNAPSHOT");
			assertThat(pom).textAtPath("/project/name").isEqualTo("demo project");
			assertThat(pom).textAtPath("/project/description")
					.isEqualTo("A demo project");
		});
	}

	@Test
	void pomWithParent() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.parent("org.springframework.boot", "spring-boot-starter-parent",
				"2.1.0.RELEASE");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/parent/groupId")
					.isEqualTo("org.springframework.boot");
			assertThat(pom).textAtPath("/project/parent/artifactId")
					.isEqualTo("spring-boot-starter-parent");
			assertThat(pom).textAtPath("/project/parent/version")
					.isEqualTo("2.1.0.RELEASE");
		});
	}

	@Test
	void pomWithProperties() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.setProperty("java.version", "1.8");
		build.setProperty("alpha", "a");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/properties/java.version")
					.isEqualTo("1.8");
			assertThat(pom).textAtPath("/project/properties/alpha").isEqualTo("a");
		});
	}

	@Test
	void pomWithVersionProperties() throws Exception {
		MavenBuild build = new MavenBuild();
		build.addVersionProperty(VersionProperty.of("version.property"), "1.2.3");
		build.addInternalVersionProperty("internal.property", "4.5.6");
		build.addExternalVersionProperty("external.property", "7.8.9");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/properties/version.property")
					.isEqualTo("1.2.3");
			assertThat(pom).textAtPath("/project/properties/internal.property")
					.isEqualTo("4.5.6");
			assertThat(pom).textAtPath("/project/properties/external.property")
					.isEqualTo("7.8.9");
		});
	}

	@Test
	void pomWithAnnotationProcessorDependency() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.dependencies().add("annotation-processor", "org.springframework.boot",
				"spring-boot-configuration-processor",
				DependencyType.ANNOTATION_PROCESSOR);
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId")
					.isEqualTo("org.springframework.boot");
			assertThat(dependency).textAtPath("artifactId")
					.isEqualTo("spring-boot-configuration-processor");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isNullOrEmpty();
			assertThat(dependency).textAtPath("optional").isEqualTo("true");
		});
	}

	@Test
	void pomWithCompileDependency() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.dependencies().add("root", "org.springframework.boot",
				"spring-boot-starter", DependencyType.COMPILE);
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId")
					.isEqualTo("org.springframework.boot");
			assertThat(dependency).textAtPath("artifactId")
					.isEqualTo("spring-boot-starter");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isNullOrEmpty();
			assertThat(dependency).textAtPath("optional").isNullOrEmpty();
		});
	}

	@Test
	void pomWithProvidedRuntimeDependency() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.dependencies().add("tomcat", "org.springframework.boot",
				"spring-boot-starter-tomcat", DependencyType.PROVIDED_RUNTIME);
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId")
					.isEqualTo("org.springframework.boot");
			assertThat(dependency).textAtPath("artifactId")
					.isEqualTo("spring-boot-starter-tomcat");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isEqualTo("provided");
			assertThat(dependency).textAtPath("optional").isNullOrEmpty();
		});
	}

	@Test
	void pomWithRuntimeDependency() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.dependencies().add("hikari", "com.zaxxer", "HikariCP",
				DependencyType.RUNTIME);
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId").isEqualTo("com.zaxxer");
			assertThat(dependency).textAtPath("artifactId").isEqualTo("HikariCP");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isEqualTo("runtime");
			assertThat(dependency).textAtPath("optional").isNullOrEmpty();
		});
	}

	@Test
	void pomWithTestCompileDependency() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.dependencies().add("test", "org.springframework.boot",
				"spring-boot-starter-test", DependencyType.TEST_COMPILE);
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId")
					.isEqualTo("org.springframework.boot");
			assertThat(dependency).textAtPath("artifactId")
					.isEqualTo("spring-boot-starter-test");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isEqualTo("test");
			assertThat(dependency).textAtPath("optional").isNullOrEmpty();
		});
	}

	@Test
	void pomWithTestRuntimeDependency() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.dependencies().add("embed-mongo", "de.flapdoodle.embed",
				"de.flapdoodle.embed.mongo", DependencyType.TEST_RUNTIME);
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId").isEqualTo("de.flapdoodle.embed");
			assertThat(dependency).textAtPath("artifactId")
					.isEqualTo("de.flapdoodle.embed.mongo");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isEqualTo("test");
			assertThat(dependency).textAtPath("optional").isNullOrEmpty();
		});
	}

	@Test
	void pomWithBom() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.addBom(new BillOfMaterials("com.example", "my-project-dependencies",
				VersionReference.ofValue("1.0.0.RELEASE")));
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom
					.nodeAtPath("/project/dependencyManagement/dependencies/dependency");
			assertBom(dependency, "com.example", "my-project-dependencies",
					"1.0.0.RELEASE");
		});
	}

	@Test
	void pomWithOrderedBoms() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.addBom(new BillOfMaterials("com.example", "my-project-dependencies",
				VersionReference.ofValue("1.0.0.RELEASE"), 5));
		build.addBom(new BillOfMaterials("com.example", "root-dependencies",
				VersionReference.ofProperty("root.version"), 2));
		generatePom(build, (pom) -> {
			NodeAssert dependencies = pom
					.nodeAtPath("/project/dependencyManagement/dependencies");
			NodeAssert firstBom = assertThat(dependencies).nodeAtPath("dependency[1]");
			assertBom(firstBom, "com.example", "root-dependencies", "${root.version}");
			NodeAssert secondBom = assertThat(dependencies).nodeAtPath("dependency[2]");
			assertBom(secondBom, "com.example", "my-project-dependencies",
					"1.0.0.RELEASE");
		});
	}

	private void assertBom(NodeAssert firstBom, String groupId, String artifactId,
			String version) {
		assertThat(firstBom).textAtPath("groupId").isEqualTo(groupId);
		assertThat(firstBom).textAtPath("artifactId").isEqualTo(artifactId);

		assertThat(firstBom).textAtPath("version").isEqualTo(version);
		assertThat(firstBom).textAtPath("type").isEqualTo("pom");
		assertThat(firstBom).textAtPath("scope").isEqualTo("import");
	}

	@Test
	void pomWithPlugin() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.plugin("org.springframework.boot", "spring-boot-maven-plugin");
		generatePom(build, (pom) -> {
			NodeAssert plugin = pom.nodeAtPath("/project/build/plugins/plugin");
			assertThat(plugin).textAtPath("groupId")
					.isEqualTo("org.springframework.boot");
			assertThat(plugin).textAtPath("artifactId")
					.isEqualTo("spring-boot-maven-plugin");
			assertThat(plugin).textAtPath("version").isNullOrEmpty();
		});
	}

	@Test
	void pomWithPluginWithConfiguration() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		MavenPlugin kotlin = build.plugin("org.jetbrains.kotlin", "kotlin-maven-plugin");
		kotlin.configuration((configuration) -> {
			configuration.add("args", (args) -> {
				args.add("arg", "-Xjsr305=strict");
			});
			configuration.add("compilerPlugins", (compilerPlugins) -> {
				compilerPlugins.add("plugin", "spring");
			});
		});
		generatePom(build, (pom) -> {
			NodeAssert plugin = pom.nodeAtPath("/project/build/plugins/plugin");
			assertThat(plugin).textAtPath("groupId").isEqualTo("org.jetbrains.kotlin");
			assertThat(plugin).textAtPath("artifactId").isEqualTo("kotlin-maven-plugin");
			assertThat(plugin).textAtPath("version").isNullOrEmpty();
			NodeAssert configuration = plugin.nodeAtPath("configuration");
			assertThat(configuration).textAtPath("args/arg").isEqualTo("-Xjsr305=strict");
			assertThat(configuration).textAtPath("compilerPlugins/plugin")
					.isEqualTo("spring");
		});
	}

	@Test
	void pomWithPluginWithExecution() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		MavenPlugin asciidoctor = build.plugin("org.asciidoctor",
				"asciidoctor-maven-plugin", "1.5.3");
		asciidoctor.execution("generate-docs", (execution) -> {
			execution.goal("process-asciidoc");
			execution.configuration((configuration) -> {
				configuration.add("doctype", "book");
				configuration.add("backend", "html");
			});
		});
		generatePom(build, (pom) -> {
			NodeAssert plugin = pom.nodeAtPath("/project/build/plugins/plugin");
			assertThat(plugin).textAtPath("groupId").isEqualTo("org.asciidoctor");
			assertThat(plugin).textAtPath("artifactId")
					.isEqualTo("asciidoctor-maven-plugin");
			assertThat(plugin).textAtPath("version").isEqualTo("1.5.3");
			NodeAssert execution = plugin.nodeAtPath("executions/execution");
			assertThat(execution).textAtPath("id").isEqualTo("generate-docs");
			assertThat(execution).textAtPath("goals/goal").isEqualTo("process-asciidoc");
			NodeAssert configuration = execution.nodeAtPath("configuration");
			assertThat(configuration).textAtPath("doctype").isEqualTo("book");
			assertThat(configuration).textAtPath("backend").isEqualTo("html");
		});
	}

	@Test
	void pomWithMavenCentral() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.addRepository(MavenRepository.MAVEN_CENTRAL);
		generatePom(build, (pom) -> {
			assertThat(pom).nodeAtPath("/project/repositories").isNull();
			assertThat(pom).nodeAtPath("/project/pluginRepositories").isNull();
		});
	}

	@Test
	void pomWithRepository() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.addRepository("spring-milestones", "Spring Milestones",
				"https://repo.spring.io/milestone");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/repositories/repository/id")
					.isEqualTo("spring-milestones");
			assertThat(pom).textAtPath("/project/repositories/repository/name")
					.isEqualTo("Spring Milestones");
			assertThat(pom).textAtPath("/project/repositories/repository/url")
					.isEqualTo("https://repo.spring.io/milestone");
			assertThat(pom).nodeAtPath("/project/repositories/repository/snapshots")
					.isNull();
			assertThat(pom).nodeAtPath("/project/pluginRepositories").isNull();
		});
	}

	@Test
	void pomWithPluginRepository() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.addPluginRepository("spring-milestones", "Spring Milestones",
				"https://repo.spring.io/milestone");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/pluginRepositories/pluginRepository/id")
					.isEqualTo("spring-milestones");
			assertThat(pom)
					.textAtPath("/project/pluginRepositories/pluginRepository/name")
					.isEqualTo("Spring Milestones");
			assertThat(pom).textAtPath("/project/pluginRepositories/pluginRepository/url")
					.isEqualTo("https://repo.spring.io/milestone");
			assertThat(pom).nodeAtPath("/project/repositories/repository/snapshots")
					.isNull();
			assertThat(pom).nodeAtPath("/project/repositories").isNull();
		});
	}

	@Test
	void pomWithSnapshotRepository() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.addRepository("spring-snapshots", "Spring Snapshots",
				"https://repo.spring.io/snapshot", true);
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/repositories/repository/id")
					.isEqualTo("spring-snapshots");
			assertThat(pom).textAtPath("/project/repositories/repository/name")
					.isEqualTo("Spring Snapshots");
			assertThat(pom).textAtPath("/project/repositories/repository/url")
					.isEqualTo("https://repo.spring.io/snapshot");
			assertThat(pom)
					.textAtPath("/project/repositories/repository/snapshots/enabled")
					.isEqualTo("true");
			assertThat(pom).nodeAtPath("/project/pluginRepositories").isNull();
		});
	}

	@Test
	void pomWithSnapshotPluginRepository() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.addPluginRepository("spring-snapshots", "Spring Snapshots",
				"https://repo.spring.io/snapshot", true);
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/pluginRepositories/pluginRepository/id")
					.isEqualTo("spring-snapshots");
			assertThat(pom)
					.textAtPath("/project/pluginRepositories/pluginRepository/name")
					.isEqualTo("Spring Snapshots");
			assertThat(pom).textAtPath("/project/pluginRepositories/pluginRepository/url")
					.isEqualTo("https://repo.spring.io/snapshot");
			assertThat(pom).textAtPath(
					"/project/pluginRepositories/pluginRepository/snapshots/enabled")
					.isEqualTo("true");
			assertThat(pom).nodeAtPath("/project/repositories").isNull();
		});
	}

	@Test
	void pomWithCustomSourceDirectories() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.setSourceDirectory("${project.basedir}/src/main/kotlin");
		build.setTestSourceDirectory("${project.basedir}/src/test/kotlin");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/build/sourceDirectory")
					.isEqualTo("${project.basedir}/src/main/kotlin");
			assertThat(pom).textAtPath("/project/build/testSourceDirectory")
					.isEqualTo("${project.basedir}/src/test/kotlin");
		});
	}

	private void generatePom(MavenBuild mavenBuild, Consumer<NodeAssert> consumer)
			throws Exception {
		MavenBuildWriter writer = new MavenBuildWriter();
		StringWriter out = new StringWriter();
		writer.writeTo(new IndentingWriter(out), mavenBuild);
		consumer.accept(new NodeAssert(out.toString()));
	}

}
