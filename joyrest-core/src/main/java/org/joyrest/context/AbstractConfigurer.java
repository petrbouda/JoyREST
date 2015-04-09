package org.joyrest.context;

import org.joyrest.aspect.Aspect;
import org.joyrest.collection.DefaultMultiMap;
import org.joyrest.collection.JoyCollections;
import org.joyrest.exception.ExceptionConfiguration;
import org.joyrest.exception.type.InvalidConfigurationException;
import org.joyrest.logging.JoyLogger;
import org.joyrest.model.http.MediaType;
import org.joyrest.routing.EntityRoute;
import org.joyrest.routing.ControllerConfiguration;
import org.joyrest.routing.Route;
import org.joyrest.transform.Reader;
import org.joyrest.transform.ReaderRegistrar;
import org.joyrest.transform.TransformerRegistrar;
import org.joyrest.transform.WriterRegistrar;
import org.joyrest.transform.aspect.SerializationAspect;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractConfigurer<T> implements Configurer<T> {

	private final static JoyLogger log = new JoyLogger(EntityRoute.class);

	/* ServiceLocator name in its own context */
	public static final String JOY_REST_BEAN_CONTEXT = "JoyRestBeanContext";

	protected abstract <B> List<B> getBeans(Class<B> clazz);

	protected ApplicationContext initializeContext() {
		DefaultMultiMap<MediaType, WriterRegistrar> writers =
			createTransformerMap(getBeans(WriterRegistrar.class));
		DefaultMultiMap<MediaType, ReaderRegistrar> readers =
			createTransformerMap(getBeans(ReaderRegistrar.class));

		List<ExceptionConfiguration> handlers = getBeans(ExceptionConfiguration.class);
		handlers.forEach(ExceptionConfiguration::initialize);

		List<ControllerConfiguration> routers = getBeans(ControllerConfiguration.class);
		Aspect[] requiredAspects = getRequiredAspects(writers);

		Set<Route<?, ?>> routes = routers.stream()
			.peek(ControllerConfiguration::initialize)
			.flatMap(config -> config.getRoutes().stream())
			.peek(route -> log.info(() -> String.format(
				"Route instantiated: METHOD[%s], PATH[%s], CONSUMES[%s], PRODUCES[%s], REQ-CLASS[%s], RESP-CLASS[%s]",
				route.getHttpMethod(), route.getPath(), route.getConsumes(), route.getProduces(),
				getSimpleName(route.getRequestBodyClass()), getSimpleName(route.getResponseBodyClass()))))
			.collect(Collectors.toSet());

		routes.stream()
			.map(route -> (EntityRoute) route)
			.forEach(route -> {
				populateReader(route, readers);
				route.aspect(requiredAspects);
			});

		ApplicationContext joyContext = new ApplicationContextImpl();
		joyContext.addRoutes(routes);
		joyContext.setWriters(writers);
		joyContext.addExceptionHandlers(handlers);
		return joyContext;
	}

	private <X extends TransformerRegistrar<?>> DefaultMultiMap<MediaType, X> createTransformerMap(List<X> registrars) {
		DefaultMultiMap<MediaType, X> map = JoyCollections.defaultHashMultiMap();
		registrars.stream().forEach(registrar -> map.add(registrar.getMediaType(), registrar));
		return map;
	}

	private Aspect<?, ?>[] getRequiredAspects(DefaultMultiMap<MediaType, WriterRegistrar> writers) {
		return new Aspect<?, ?>[]{new SerializationAspect(writers)};
	}

	private String getSimpleName(Class<?> clazz) {
		return clazz == null ? "none" : clazz.getSimpleName();
	}

	private void populateReader(Route route, DefaultMultiMap<MediaType, ReaderRegistrar> registrars) {
		Map<MediaType, Reader> readers = new HashMap<>();

		if (route.getRequestBodyClass() == null) {

			List consumes = route.getConsumes();
			for (int i=0; i < consumes.size(); ++i) {
				MediaType mediaType = (MediaType) consumes.get(i);

				// Find Reader for dedicated to entity
				List<ReaderRegistrar> allNonDefault = registrars.getAllNonDefault(mediaType);
				Optional<ReaderRegistrar> optRegistrar = allNonDefault.stream().filter(
					registrar -> registrar.getEntityClass().get() == route.getRequestBodyClass()).findFirst();

				// Find Reader for dedicated to entity
				if (optRegistrar.isPresent()) {
					readers.put(mediaType, optRegistrar.get().getTransformer());
					continue;
				}

				// Find Default Reader for given media-type
				ReaderRegistrar registrar = registrars.getDefault(mediaType).orElseThrow(
					() -> new InvalidConfigurationException(
						String.format("No reader for media-type '%s' and class '%s'",
							mediaType.getValue(), route.getRequestBodyClass())));
				readers.put(mediaType, registrar.getTransformer());
			}
		}
		route.setReaders(readers);
	}

}
