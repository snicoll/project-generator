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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An annotation.
 *
 * @author Andy Wilkinson
 */
public final class Annotation {

	private final String name;

	private final Map<String, AttributeValues> attributes;

	private Annotation(Builder builder) {
		this.name = builder.name;
		this.attributes = new LinkedHashMap<>(builder.attributes);
	}

	public String getName() {
		return this.name;
	}

	public Map<String, AttributeValues> getAttributes() {
		return this.attributes;
	}

	public static Builder name(String name) {
		return new Builder(name);
	}

	/**
	 * Builder for creating an {@link Annotation}.
	 */
	public static final class Builder {

		private final String name;

		private final Map<String, AttributeValues> attributes = new LinkedHashMap<>();

		private Builder(String name) {
			this.name = name;
		}

		public Builder attribute(String name, Class<?> type, String... values) {
			this.attributes.put(name, new AttributeValues(type, values));
			return this;
		}

		public Annotation build() {
			return new Annotation(this);
		}

	}

	/**
	 * Define the values of a particular attribute of an annotation.
	 */
	public static final class AttributeValues {

		private final Class<?> type;

		private final List<String> values;

		private AttributeValues(Class<?> type, String... values) {
			this.type = type;
			this.values = Arrays.asList(values);
		}

		public Class<?> getType() {
			return this.type;
		}

		public List<String> getValues() {
			return this.values;
		}

	}

}
