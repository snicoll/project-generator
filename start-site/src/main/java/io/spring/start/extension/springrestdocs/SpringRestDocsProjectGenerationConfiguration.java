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

package io.spring.start.extension.springrestdocs;

import io.spring.initializr.generator.buildsystem.gradle.ConditionalOnGradle;
import io.spring.initializr.generator.buildsystem.maven.ConditionalOnMaven;
import io.spring.initializr.generator.condition.ConditionalOnDependency;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;

import org.springframework.context.annotation.Bean;

/**
 * Configuration for generation of projects that depend on Spring REST Docs.
 *
 * @author Andy Wilkinson
 */
@ProjectGenerationConfiguration
@ConditionalOnDependency(groupId = "org.springframework.restdocs", artifactId = "spring-restdocs-mockmvc")
public class SpringRestDocsProjectGenerationConfiguration {

	@Bean
	@ConditionalOnGradle
	public SpringRestDocsGradleBuildCustomizer restDocsGradleBuildCustomizer() {
		return new SpringRestDocsGradleBuildCustomizer();
	}

	@Bean
	@ConditionalOnMaven
	public SpringRestDocsMavenBuildCustomizer restDocsMavenBuildCustomizer() {
		return new SpringRestDocsMavenBuildCustomizer();
	}

}
