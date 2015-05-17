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
package org.joyrest.context;

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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joyrest.aspect.Aspect;
import org.joyrest.exception.configuration.ExceptionConfiguration;
import org.joyrest.exception.configuration.TypedExceptionConfiguration;
import org.joyrest.exception.handler.InternalExceptionHandler;
import org.joyrest.exception.type.RestException;
import org.joyrest.routing.ControllerConfiguration;
import org.joyrest.routing.InternalRoute;
import org.joyrest.transform.Reader;
import org.joyrest.transform.StringReaderWriter;
import org.joyrest.transform.Writer;
import org.joyrest.transform.aspect.SerializationAspect;

/**
 * Abstract class as a helper for initialization an {@link ApplicationContext}.
 *
 * @param <T> type of configuration class which is used to set up a configurer
 * @see Configurer
 * @see DependencyInjectionConfigurer
 * @see NonDiConfigurer
 * @author pbouda
 */
public abstract class AbstractConfigurer<T> implements Configurer<T> {

	private final StringReaderWriter stringReaderWriter = new StringReaderWriter();

	protected final List<Aspect> REQUIRED_ASPECTS = singletonList(new SerializationAspect());
	protected final List<Reader> REQUIRED_READERS = singletonList(stringReaderWriter);
	protected final List<Writer> REQUIRED_WRITERS = singletonList(stringReaderWriter);

	/**
	 * Returns all {@link Aspect aspects} registered in the application context
	 *
	 * @return all registered aspects
	 */
	protected abstract Collection<Aspect> getAspects();

	/**
	 * Returns all {@link Reader readers} registered in the application context
	 *
	 * @return all registered readers
	 */
	protected abstract Collection<Reader> getReaders();

	/**
	 * Returns all {@link Writer writers} registered in the application context
	 *
	 * @return all registered writers
	 */
	protected abstract Collection<Writer> getWriters();

	/**
	 * Returns all {@link ExceptionConfiguration exceptionConfigurations} registered in the application context
	 *
	 * @return all registered exceptionConfigurations
	 */
	protected abstract Collection<ExceptionConfiguration> getExceptionConfigurations();

	/**
	 * Returns all {@link ControllerConfiguration controllerConfigurations} registered in the application context
	 *
	 * @return all registered controllerConfigurations
	 */
	protected abstract Collection<ControllerConfiguration> getControllerConfiguration();

	/**
	 * Method causes the initialization of the application context using the methods which returns a collection of beans such as
	 *
	 * @see AbstractConfigurer#getAspects()
	 * @see AbstractConfigurer#getReaders()
	 * @see AbstractConfigurer#getWriters()
	 * @see AbstractConfigurer#getExceptionConfigurations()
	 * @see AbstractConfigurer#getControllerConfiguration()
	 *
	 * @return initialized {@code application context}
	 */
	protected ApplicationContext initializeContext() {
		Map<Boolean, List<Reader>> readers = createTransformers(getReaders(), REQUIRED_READERS);
		Map<Boolean, List<Writer>> writers = createTransformers(getWriters(), REQUIRED_WRITERS);
		Collection<Aspect> aspects = sort(insertInto(getAspects(), REQUIRED_ASPECTS));

		orderDuplicationCheck(aspects);

		Map<Class<? extends Exception>, InternalExceptionHandler> handlers =
				insertInto(getExceptionConfigurations(), new InternalExceptionConfiguration()).stream()
					.peek(ExceptionConfiguration::initialize)
					.flatMap(config -> config.getExceptionHandlers().stream())
					.peek(handler -> {
						populateHandlerWriters(writers, handler, nonNull(handler.getResponseType()));
						logExceptionHandler(handler);
					})
					.collect(toMap(InternalExceptionHandler::getExceptionClass, identity()));

		Set<InternalRoute> routes = getControllerConfiguration().stream()
			.peek(ControllerConfiguration::initialize)
			.flatMap(config -> config.getRoutes().stream())
			.peek(route -> {
				route.aspect(aspects.toArray(new Aspect[aspects.size()]));
				populateRouteReaders(readers, route);
				populateRouteWriters(writers, route);
				logRoute(route);
			}).collect(toSet());

		ApplicationContextImpl context = new ApplicationContextImpl();
		context.setRoutes(routes);
		context.setExceptionHandlers(handlers);
		return context;
	}

	/**
	 * Internal implementation of {@link RestException} handler.
	 * */
	private class InternalExceptionConfiguration extends TypedExceptionConfiguration {

		@Override
		protected void configure() {
			handle(RestException.class, (req, resp, ex) -> {
				resp.status(ex.getStatus());
				ex.getHeaders()
					.forEach(resp::header);
			});
		}
	}

}
