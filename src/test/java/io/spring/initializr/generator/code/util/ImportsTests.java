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

package io.spring.initializr.generator.code.util;

import java.util.Comparator;
import java.util.Set;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Imports}.
 *
 * @author Stephane Nicoll
 */
public class ImportsTests {

	@Test
	public void extractWithDefaultComparator() {
		Set<String> actual = Imports.of("com.example.B", "com.example.A", "org.acme.C")
				.extract((type) -> type.startsWith("com.example."));
		assertThat(actual).containsExactly("com.example.A", "com.example.B");
	}

	@Test
	public void extractWithCustomComparator() {
		Set<String> actual = Imports.of("com.example.B", "com.example.A", "org.acme.C")
				.extract((type) -> type.startsWith("com.example."),
						Comparator.reverseOrder());
		assertThat(actual).containsExactly("com.example.B", "com.example.A");
	}

	@Test
	public void extractNotMatching() {
		Set<String> actual = Imports.of("com.example.B", "com.example.A", "org.acme.C")
				.extract((type) -> type.startsWith("org.springframework."));
		assertThat(actual).isEmpty();
	}

	@Test
	public void extractRemovesEntries() {
		Imports imports = Imports.of("com.example.B", "com.example.A", "org.acme.C");
		assertThat(imports.extract((type) -> type.startsWith("com.example."))).hasSize(2);
		assertThat(imports.extract((type) -> type.startsWith("com.example."))).isEmpty();
	}

}
