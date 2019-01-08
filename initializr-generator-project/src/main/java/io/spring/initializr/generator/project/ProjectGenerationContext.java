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

package io.spring.initializr.generator.project;

import io.spring.initializr.generator.ResolvedProjectDescription;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Provide configuration and infrastructure to generate a project.
 *
 * @author Stephane Nicoll
 */
public class ProjectGenerationContext extends AnnotationConfigApplicationContext {

	private final ResolvedProjectDescription projectDescription;

	public ProjectGenerationContext(ResolvedProjectDescription projectDescription) {
		this.projectDescription = projectDescription;
	}

	public ResolvedProjectDescription getProjectDescription() {
		return this.projectDescription;
	}

}
