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

package io.spring.initializr.generator.project.build;

import io.spring.initializr.generator.ProjectDescription;
import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.DependencyType;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;

import org.springframework.context.annotation.Bean;

/**
 * Project generation configuration for projects using any build system.
 *
 * @author Andy Wilkinson
 */
@ProjectGenerationConfiguration
public class BuildProjectGenerationConfiguration {

	@Bean
	public BuildCustomizer<Build> testStarterContributor() {
		return (build) -> build.dependencies().add("test", "org.springframework.boot",
				"spring-boot-starter-test", DependencyType.TEST_COMPILE);
	}

	@Bean
	public ProjectDescriptionBuildCustomizer projectDescriptionBuildCustomizer(
			ProjectDescription projectDescription) {
		return new ProjectDescriptionBuildCustomizer(projectDescription);
	}

	@Bean
	public SpringBootVersionRepositoriesBuildCustomizer repositoriesBuilderCustomizer(
			ProjectDescription description) {
		return new SpringBootVersionRepositoriesBuildCustomizer(
				description.getPlatformVersion());
	}

}
