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

import java.io.File;
import java.util.Objects;

import io.spring.initializr.generator.language.Language;

import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * A build system that can be used by a generated project.
 *
 * @author Andy Wilkinson
 */
public interface BuildSystem {

	/**
	 * The id of the build system.
	 * @return the id
	 */
	String id();

	default File getMainDirectory(File projectRoot, Language language) {
		return new File(projectRoot, "src/main/" + language.id());
	}

	default File getTestDirectory(File projectRoot, Language language) {
		return new File(projectRoot, "src/test/" + language.id());
	}

	static BuildSystem forId(String id) {
		return SpringFactoriesLoader
				.loadFactories(BuildSystemFactory.class,
						BuildSystem.class.getClassLoader())
				.stream().map((factory) -> factory.createBuildSystem(id))
				.filter(Objects::nonNull).findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"Unrecognized build system id '" + id + "'"));
	}

}
