/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.generator.language;

import java.nio.file.Path;

import io.spring.initializr.generator.language.groovy.GroovyLanguage;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.test.project.ProjectGenerationTester;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConditionalOnLanguage}.
 *
 * @author Stephane Nicoll
 */
@ExtendWith(TempDirectory.class)
class ConditionalOnLanguageTests {

	private final ProjectGenerationTester projectGenerationTester;

	ConditionalOnLanguageTests(@TempDir Path directory) {
		this.projectGenerationTester = new ProjectGenerationTester(ProjectGenerationTester
				.defaultProjectGenerationContext(directory)
				.andThen((context) -> context.register(LanguageTestConfiguration.class)));
	}

	@Test
	void outcomeWithJavaLanguage() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setLanguage(new JavaLanguage());
		String bean = outcomeFor(projectDescription);
		assertThat(bean).isEqualTo("testJava");
	}

	@Test
	void outcomeWithGroovyBuildSystem() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setLanguage(new GroovyLanguage());
		String bean = outcomeFor(projectDescription);
		assertThat(bean).isEqualTo("testGroovy");
	}

	@Test
	void outcomeWithNoMatch() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setLanguage(new KotlinLanguage());
		this.projectGenerationTester.generate(projectDescription,
				(projectGenerationContext) -> {
					assertThat(projectGenerationContext.getBeansOfType(String.class))
							.isEmpty();
					return null;
				});
	}

	@Test
	void outcomeWithNoAvailableLanguage() {
		ProjectDescription projectDescription = new ProjectDescription();
		this.projectGenerationTester.generate(projectDescription,
				(projectGenerationContext) -> {
					assertThat(projectGenerationContext.getBeansOfType(String.class))
							.isEmpty();
					return null;
				});
	}

	private String outcomeFor(ProjectDescription projectDescription) {
		return this.projectGenerationTester.generate(projectDescription,
				(projectGenerationContext) -> {
					assertThat(projectGenerationContext.getBeansOfType(String.class))
							.hasSize(1);
					return projectGenerationContext.getBean(String.class);
				});
	}

	@Configuration
	static class LanguageTestConfiguration {

		@Bean
		@ConditionalOnLanguage("java")
		public String java() {
			return "testJava";
		}

		@Bean
		@ConditionalOnLanguage("groovy")
		public String groovy() {
			return "testGroovy";
		}

	}

}
