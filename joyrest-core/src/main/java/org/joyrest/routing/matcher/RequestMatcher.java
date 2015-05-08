package org.joyrest.routing.matcher;

import static java.util.stream.Collectors.toList;
import static org.joyrest.model.http.HeaderName.ACCEPT;
import static org.joyrest.model.http.HeaderName.CONTENT_TYPE;
import static org.joyrest.model.http.MediaType.WILDCARD;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.joyrest.model.http.HeaderName;
import org.joyrest.model.http.MediaType;
import org.joyrest.model.request.InternalRequest;
import org.joyrest.routing.InternalRoute;

public final class RequestMatcher {

	private RequestMatcher() {
	}

	public static boolean matchProduces(InternalRoute route, InternalRequest<?> request) {
		if(route.getProduces().contains(WILDCARD))
			return true;

		Optional<String> optAccept = request.getHeader(ACCEPT);
		if (optAccept.isPresent()) {
			List<MediaType> acceptTypes = getAcceptMediaTypes(route, optAccept);

			if (!acceptTypes.isEmpty()) {
				if (acceptTypes.size() == 1) {
					request.setMatchedAccept(acceptTypes.get(0));
				} else {
					// If there are more than one accept media-type and the incoming request contains also
					// content type, so choose the same one media-type
					request.setMatchedAccept(acceptTypes.get(0));
					request.getHeader(HeaderName.CONTENT_TYPE).ifPresent(contentTypeStr -> {
						MediaType contentType = MediaType.of(contentTypeStr);
						if (acceptTypes.contains(contentType))
							request.setMatchedAccept(contentType);
					});
				}

				return true;
			}
		}

		return false;
	}

	private static List<MediaType> getAcceptMediaTypes(InternalRoute route, Optional<String> optAccept) {
		return Arrays.stream(optAccept.get().split(","))
					.filter(Objects::nonNull)
					.map(String::trim)
					.map(MediaType::of)
					.distinct()
					.filter(accept -> route.getProduces().contains(accept))
					.collect(toList());
	}

	public static boolean matchConsumes(InternalRoute route, InternalRequest<?> request) {
		if(route.getConsumes().contains(WILDCARD))
			return true;

		return request.getHeader(CONTENT_TYPE)
			.filter(Objects::nonNull)
			.map(MediaType::of)
			.filter(route.getConsumes()::contains)
			.isPresent();
	}

	public static boolean matchHttpMethod(InternalRoute route, InternalRequest<?> request) {
		return route.getHttpMethod() == request.getMethod();
	}
}