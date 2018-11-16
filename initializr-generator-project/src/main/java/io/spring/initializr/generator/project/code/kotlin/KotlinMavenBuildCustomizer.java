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

package io.spring.initializr.generator.project.code.kotlin;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin;
import io.spring.initializr.generator.project.build.BuildCustomizer;

/**
 * {@link BuildCustomizer} for Kotlin projects build with Maven.
 *
 * @author Andy Wilkinson
 */
class KotlinMavenBuildCustomizer implements BuildCustomizer<MavenBuild> {

	private final KotlinProjectSettings settings;

	KotlinMavenBuildCustomizer(KotlinProjectSettings kotlinProjectSettings) {
		this.settings = kotlinProjectSettings;
	}

	@Override
	public void customize(MavenBuild build) {
		build.setProperty("kotlin.version", this.settings.getVersion());
		build.setSourceDirectory("${project.basedir}/src/main/kotlin");
		build.setTestSourceDirectory("${project.basedir}/src/test/kotlin");
		MavenPlugin kotlinMavenPlugin = build.plugin("org.jetbrains.kotlin",
				"kotlin-maven-plugin", "${kotlin.version}");
		kotlinMavenPlugin.configuration((configuration) -> {
			configuration.add("args", (args) -> {
				this.settings.getCompilerArgs().forEach((arg) -> args.add("arg", arg));
			});
			configuration.add("compilerPlugins", (compilerPlugins) -> {
				compilerPlugins.add("plugin", "spring");
			});
		});
		kotlinMavenPlugin.execution("compile",
				(compile) -> compile.phase("compile").goal("compile"));
		kotlinMavenPlugin.execution("test-compile",
				(compile) -> compile.phase("test-compile").goal("test-compile"));
		kotlinMavenPlugin.dependency("org.jetbrains.kotlin", "kotlin-maven-allopen",
				"${kotlin.version}");
	}

}
