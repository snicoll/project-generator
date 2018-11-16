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

package io.spring.initializr.generator.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.spring.initializr.generator.FileContributor;
import io.spring.initializr.generator.ProjectDescription;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Main entry point for project generation.
 *
 * @author Andy Wilkinson
 */
public class ProjectGenerator {

	public File generate(ProjectDescription description) throws IOException {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.registerBean(ProjectDescription.class, () -> description);
			context.register(CoreConfiguration.class);
			context.refresh();
			Path projectRoot = Files.createTempDirectory("project-");
			context.getBean(FileContributors.class).contribute(projectRoot.toFile());
			return projectRoot.toFile();
		}
	}

	/**
	 * Configuration used to bootstrap the application context used for project
	 * generation.
	 */
	@Configuration
	@Import(ProjectGenerationImportSelector.class)
	static class CoreConfiguration {

		@Bean
		public FileContributors fileContributors(List<FileContributor> fileContributors) {
			return new FileContributors(fileContributors);
		}

	}

	/**
	 * {@link ImportSelector} for loading classes configured in {@code spring.factories}
	 * using the
	 * {@code io.spring.initializr.generator.project.ProjectGenerationConfiguration} key.
	 */
	static class ProjectGenerationImportSelector implements ImportSelector {

		@Override
		public String[] selectImports(AnnotationMetadata importingClassMetadata) {
			List<String> factories = SpringFactoriesLoader.loadFactoryNames(
					ProjectGenerationConfiguration.class, getClass().getClassLoader());
			return factories.toArray(new String[0]);
		}

	}

}
