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

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A container for items.
 *
 * @param <I> the type of the identifier
 * @param <V> the type of the item
 * @author Stephane Nicoll
 */
public class BuildItemContainer<I, V> {

	private final Map<I, V> items;

	private final Function<I, V> itemResolver;

	BuildItemContainer(Map<I, V> items, Function<I, V> itemResolver) {
		this.items = items;
		this.itemResolver = itemResolver;
	}

	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	public boolean has(I id) {
		return this.items.containsKey(id);
	}

	public Stream<I> ids() {
		return this.items.keySet().stream();
	}

	public Stream<V> values() {
		return this.items.values().stream();
	}

	public V get(I id) {
		return this.items.get(id);
	}

	public void add(I id) {
		V item = this.itemResolver.apply(id);
		if (item == null) {
			throw new IllegalArgumentException("No such value with id '" + id + "'");
		}
		add(id, item);

	}

	public V add(I id, V item) {
		return this.items.put(id, item);
	}

}
