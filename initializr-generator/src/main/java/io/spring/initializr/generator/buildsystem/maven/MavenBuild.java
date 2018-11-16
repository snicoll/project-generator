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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.spring.initializr.generator.buildsystem.Build;

/**
 * Maven build for a project.
 *
 * @author Andy Wilkinson
 */
public class MavenBuild extends Build {

	private Parent parent;

	private String sourceDirectory;

	private String testSourceDirectory;

	private final Map<String, String> properties = new TreeMap<>();

	private final List<MavenPlugin> plugins = new ArrayList<>();

	private String packaging;

	public Parent parent(String groupId, String artifactId, String version) {
		this.parent = new Parent(groupId, artifactId, version);
		return this.parent;
	}

	public Parent getParent() {
		return this.parent;
	}

	public void setProperty(String key, String value) {
		this.properties.put(key, value);
	}

	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(this.properties);
	}

	public String getSourceDirectory() {
		return this.sourceDirectory;
	}

	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public String getTestSourceDirectory() {
		return this.testSourceDirectory;
	}

	public void setTestSourceDirectory(String testSourceDirectory) {
		this.testSourceDirectory = testSourceDirectory;
	}

	public MavenPlugin plugin(String groupId, String artifactId) {
		MavenPlugin plugin = new MavenPlugin(groupId, artifactId);
		this.plugins.add(plugin);
		return plugin;
	}

	public MavenPlugin plugin(String groupId, String artifactId, String version) {
		MavenPlugin plugin = new MavenPlugin(groupId, artifactId, version);
		this.plugins.add(plugin);
		return plugin;
	}

	public List<MavenPlugin> getPlugins() {
		return Collections.unmodifiableList(this.plugins);
	}

	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public String getPackaging() {
		return this.packaging;
	}

}
