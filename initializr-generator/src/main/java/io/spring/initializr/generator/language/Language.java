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

package io.spring.initializr.generator.language;

import java.util.Objects;

import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * A language in which a generated project can be written.
 *
 * @author Andy Wilkinson
 */
public interface Language {

	String id();

	static Language forId(String id) {
		return SpringFactoriesLoader
				.loadFactories(LanguageFactory.class,
						LanguageFactory.class.getClassLoader())
				.stream().map((factory) -> factory.createLanguage(id))
				.filter(Objects::nonNull).findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"Unrecognized language id '" + id + "'"));
	}

}
