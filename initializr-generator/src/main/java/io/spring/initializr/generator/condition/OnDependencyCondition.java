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

package io.spring.initializr.generator.condition;

import io.spring.initializr.generator.Dependency;
import io.spring.initializr.generator.ProjectDescription;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@link ProjectGenerationCondition} implementation for {@link ConditionalOnDependency}.
 *
 * @author Andy Wilkinson
 */
class OnDependencyCondition extends ProjectGenerationCondition {

	@Override
	protected boolean matches(ProjectDescription projectDescription,
			ConditionContext context, AnnotatedTypeMetadata metadata) {
		String groupId = (String) metadata
				.getAnnotationAttributes(ConditionalOnDependency.class.getName())
				.get("groupId");
		String artifactId = (String) metadata
				.getAnnotationAttributes(ConditionalOnDependency.class.getName())
				.get("artifactId");
		for (Dependency dependency : projectDescription.getDependencies()) {
			if (dependency.getGroupId().equals(groupId)
					&& dependency.getArtifactId().equals(artifactId)) {
				return true;
			}
		}
		return false;
	}

}
