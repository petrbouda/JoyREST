/*
 * Copyright 2015 Petr Bouda
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joyrest.context.configurer;

import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.joyrest.context.helper.CheckHelper.orderDuplicationCheck;
import static org.joyrest.context.helper.ConfigurationHelper.createTransformers;
import static org.joyrest.context.helper.ConfigurationHelper.sort;
import static org.joyrest.context.helper.LoggingHelper.logExceptionHandler;
import static org.joyrest.context.helper.LoggingHelper.logRoute;
import static org.joyrest.context.helper.PopulateHelper.*;
import static org.joyrest.utils.CollectionUtils.insertInto;
import static org.joyrest.utils.CollectionUtils.insertIntoNewList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joyrest.aspect.Interceptor;
import org.joyrest.context.ApplicationContext;
import org.joyrest.context.ApplicationContextImpl;
import org.joyrest.context.autoconfigurar.AutoConfigurer;
import org.joyrest.exception.configuration.ExceptionConfiguration;
import org.joyrest.exception.configuration.TypedExceptionConfiguration;
import org.joyrest.exception.handler.InternalExceptionHandler;
import org.joyrest.exception.type.RestException;
import org.joyrest.routing.ControllerConfiguration;
import org.joyrest.routing.InternalRoute;
import org.joyrest.transform.AbstractReaderWriter;
import org.joyrest.transform.Reader;
import org.joyrest.transform.Writer;
import org.joyrest.transform.interceptor.SerializationInterceptor;
import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 * Abstract class as a helper for initialization an {@link ApplicationContext}.
 *
 * @param <T> type of configurer class which is used to set up a configurer
 * @see Configurer
 * @see NonDiConfigurer
 * @author pbouda
 */
public abstract class AbstractConfigurer<T> implements Configurer<T> {

	protected final List<Interceptor> PREDEFINED_ASPECTS = singletonList(new SerializationInterceptor());

	/**
	 * Method causes the initialization of the application context using the methods which returns a collection of beans such as
	 *
	 * @return initialized {@code application context}
	 */
	protected ApplicationContext initializeContext() {
		List<AbstractReaderWriter> readersWriters = AutoConfigurer.configureReadersWriters();
		Map<Boolean, List<Reader>> readers = createTransformers(insertIntoNewList(getBeans(Reader.class), readersWriters));
		Map<Boolean, List<Writer>> writers = createTransformers(insertIntoNewList(getBeans(Writer.class), readersWriters));
		Collection<Interceptor> interceptors = sort(insertIntoNewList(getBeans(Interceptor.class), PREDEFINED_ASPECTS));

		orderDuplicationCheck(interceptors);

		Map<Class<? extends Exception>, InternalExceptionHandler> handlers =
				insertInto(getBeans(ExceptionConfiguration.class), new InternalExceptionConfiguration()).stream()
					.peek(ExceptionConfiguration::initialize)
					.flatMap(config -> config.getExceptionHandlers().stream())
					.peek(handler -> {
						populateHandlerWriters(writers, handler, nonNull(handler.getResponseType()));
						logExceptionHandler(handler);
					})
					.collect(toMap(InternalExceptionHandler::getExceptionClass, identity()));

		Set<InternalRoute> routes = getBeans(ControllerConfiguration.class).stream()
			.peek(ControllerConfiguration::initialize)
			.flatMap(config -> config.getRoutes().stream())
			.peek(route -> {
				route.aspect(interceptors.toArray(new Interceptor[interceptors.size()]));
				populateRouteReaders(readers, route);
				populateRouteWriters(writers, route);
				logRoute(route);
			}).collect(toSet());

		ApplicationContextImpl context = new ApplicationContextImpl();
		context.setRoutes(routes);
		context.setExceptionHandlers(handlers);
		return context;
	}

}
