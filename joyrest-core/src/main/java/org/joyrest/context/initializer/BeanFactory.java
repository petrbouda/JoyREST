/*
 * Copyright 2015 Petr Bouda
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
package org.joyrest.context.initializer;

import java.util.Collection;
import java.util.function.Function;

public class BeanFactory {

	private final Function<Class<Object>, Collection<Object>> beanFactory;

	public BeanFactory(Function<Class<Object>, Collection<Object>> beanFactory) {
		this.beanFactory = beanFactory;
	}

	@SuppressWarnings("unchecked")
	public <B> Collection<B> get(Class<B> clazz) {
		return (Collection<B>) beanFactory.apply((Class<Object>) clazz);
	}
}