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

import io.spring.initializr.generator.language.groovy.GroovyLanguage;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link Language}
 *
 * @author Stephane Nicoll
 */
class LanguageTests {

	@Test
	void javaLanguage() {
		Language java = Language.forId("java");
		assertThat(java).isInstanceOf(JavaLanguage.class);
		assertThat(java.id()).isEqualTo("java");
		assertThat(java.toString()).isEqualTo("java");
	}

	@Test
	void groovyLanguage() {
		Language groovy = Language.forId("groovy");
		assertThat(groovy).isInstanceOf(GroovyLanguage.class);
		assertThat(groovy.id()).isEqualTo("groovy");
		assertThat(groovy.toString()).isEqualTo("groovy");
	}

	@Test
	void kotlinLanguage() {
		Language kotlin = Language.forId("kotlin");
		assertThat(kotlin).isInstanceOf(KotlinLanguage.class);
		assertThat(kotlin.id()).isEqualTo("kotlin");
		assertThat(kotlin.toString()).isEqualTo("kotlin");
	}

	@Test
	void unknownLanguage() {
		assertThatIllegalStateException().isThrownBy(() -> Language.forId("unknown"))
				.withMessageContaining("Unrecognized language id 'unknown'");
	}

}