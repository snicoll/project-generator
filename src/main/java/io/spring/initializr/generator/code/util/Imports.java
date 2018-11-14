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

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Helper for handling imports.
 *
 * @author Stephane Nicoll
 */
public class Imports {

	private final Set<String> allImports;

	private Imports(Collection<String> allImports) {
		this.allImports = new LinkedHashSet<>(allImports);
	}

	/**
	 * Create a new instance based on the specified candidates.
	 * @param candidates the candidates to use
	 * @return an instance using the specified candidate imports.
	 */
	public static Imports of(Collection<String> candidates) {
		return new Imports(candidates);
	}

	/**
	 * Create a new instance based on the specified candidates.
	 * @param candidates the candidates to use
	 * @return an instance using the specified candidate imports.
	 */
	public static Imports of(String... candidates) {
		return of(Arrays.asList(candidates));
	}

	/**
	 * Extract the imports that match the specified {@link Predicate filter} and sort them
	 * {@link String#compareTo(String) lexicographically}.
	 * @param filter the predicate to match
	 * @return the matching imports
	 */
	public Set<String> extract(Predicate<String> filter) {
		return extract(filter, String::compareTo);
	}

	/**
	 * Extract the imports that match the specified {@link Predicate filter} and sort them
	 * according to the specified {@link Comparator}.
	 * @param filter the predicate to match
	 * @param comparator the comparator to use to filter results
	 * @return the matching imports
	 */
	public Set<String> extract(Predicate<String> filter, Comparator<String> comparator) {
		Set<String> result = allImports.stream().filter(filter).sorted(comparator)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		allImports.removeAll(result);
		return result;
	}

}
