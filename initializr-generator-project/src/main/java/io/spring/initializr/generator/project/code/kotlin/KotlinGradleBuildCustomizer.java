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

import java.util.stream.Collectors;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild.TaskCustomization;
import io.spring.initializr.generator.project.build.BuildCustomizer;

/**
 * {@link BuildCustomizer} for Kotlin projects build with Gradle.
 *
 * @author Andy Wilkinson
 */
class KotlinGradleBuildCustomizer implements BuildCustomizer<GradleBuild> {

	private final KotlinProjectSettings settings;

	KotlinGradleBuildCustomizer(KotlinProjectSettings kotlinProjectSettings) {
		this.settings = kotlinProjectSettings;
	}

	@Override
	public void customize(GradleBuild build) {
		build.addPlugin("org.jetbrains.kotlin.jvm", this.settings.getVersion());
		build.addPlugin("org.jetbrains.kotlin.plugin.spring", this.settings.getVersion());
		build.customizeTask("compileKotlin", this::customizeKotlinOptions);
		build.customizeTask("compileTestKotlin", this::customizeKotlinOptions);
	}

	private void customizeKotlinOptions(TaskCustomization compile) {
		compile.nested("kotlinOptions", (kotlinOptions) -> {
			String compilerArgs = this.settings.getCompilerArgs().stream()
					.map((arg) -> "'" + arg + "'").collect(Collectors.joining(", "));
			kotlinOptions.set("freeCompilerArgs", "[" + compilerArgs + "]");
			kotlinOptions.set("jvmTarget", "'" + this.settings.getJvmTarget() + "'");
		});
	}

}
