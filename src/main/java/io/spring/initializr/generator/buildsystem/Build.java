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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.spring.initializr.generator.Dependency;
import io.spring.initializr.generator.DependencyType;

/**
 * Build configuration for a project.
 *
 * @author Andy Wilkinson
 */
public abstract class Build {

	private final List<Dependency> dependencies = new ArrayList<>();

	private String group;

	private String name;

	private String version = "0.0.1-SNAPSHOT";

	public String getGroup() {
		return this.group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void addDependency(Dependency dependency) {
		this.dependencies.add(dependency);
	}

	public Dependency addDependency(String groupId, String artifactId,
			DependencyType dependencyType) {
		Dependency dependency = new Dependency(groupId, artifactId, dependencyType);
		this.dependencies.add(dependency);
		return dependency;
	}

	public List<Dependency> getDependencies() {
		return Collections.unmodifiableList(this.dependencies);
	}

	public String getJavaVersion() {
		return "1.8";
	}

}
