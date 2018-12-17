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

package io.spring.initializr.generator.buildsystem;

import java.util.LinkedHashMap;
import java.util.function.Function;

import io.spring.initializr.generator.util.VersionReference;

/**
 * A {@link BuildItemContainer} implementation for dependencies.
 *
 * @author Stephane Nicoll
 */
public class DependencyContainer extends BuildItemContainer<String, Dependency> {

	public DependencyContainer(Function<String, Dependency> itemResolver) {
		super(new LinkedHashMap<>(), itemResolver);
	}

	public void add(String id, String groupId, String artifactId,
			DependencyType dependencyType) {
		Dependency dependency = new Dependency(groupId, artifactId, dependencyType);
		add(id, dependency);
	}

	public void add(String id, String groupId, String artifactId,
			VersionReference version, DependencyType dependencyType) {
		Dependency dependency = new Dependency(groupId, artifactId, version,
				dependencyType);
		add(id, dependency);
	}

}
