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

package io.spring.initializr.generator.build.gradle;

import io.spring.initializr.generator.ProjectDescription;
import io.spring.initializr.generator.build.BuildCustomizer;
import io.spring.initializr.generator.buildsystem.gradle.ConditionalOnGradle;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.git.GitIgnoreContributor;
import io.spring.initializr.generator.language.java.ConditionalOnJavaLanguage;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Configuration for contributions specific to the generation of a project that will use
 * Gradle as its build system.
 *
 * @author Andy Wilkinson
 */
@Configuration
@ConditionalOnGradle
public class GradleProjectGenerationConfiguration {

	@Bean
	public GradleWrapperContributor gradleWrapperContributor() {
		return new GradleWrapperContributor();
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public GitIgnoreContributor gradleGitIgnoreContributor() {
		return new GitIgnoreContributor("classpath:gradle/gitignore");
	}

	@Bean
	public GradleBuildFileContributor gradleBuildFileContributor(
			ProjectDescription projectDescription,
			ObjectProvider<BuildCustomizer<?>> buildCustomizers) {
		return new GradleBuildFileContributor(projectDescription, buildCustomizers);
	}

	@Bean
	@ConditionalOnJavaLanguage
	public BuildCustomizer<GradleBuild> javaPluginContributor() {
		return (gradleBuild) -> gradleBuild.addPlugin("java");
	}

	@Bean
	public BuildCustomizer<GradleBuild> springBootPluginContributor() {
		return (gradleBuild) -> {
			gradleBuild.addPlugin("org.springframework.boot", "2.1.0.RELEASE");
			gradleBuild.applyPlugin("io.spring.dependency-management");
		};
	}

}
