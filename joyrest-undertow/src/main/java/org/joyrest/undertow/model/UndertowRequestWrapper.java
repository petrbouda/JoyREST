package org.joyrest.undertow.model;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static org.joyrest.common.annotation.UnmodifiableMapCollector.toUnmodifiableMap;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.joyrest.model.http.HeaderName;
import org.joyrest.model.http.HttpMethod;
import org.joyrest.model.http.MediaType;
import org.joyrest.model.request.InternalRequest;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class UndertowRequestWrapper extends InternalRequest<Object> {

	private final HttpServerExchange request;

	private final HttpMethod method;

	private final String path;

	private Map<HeaderName, String> headers;

	private Map<String, String[]> queryParams;

	protected Optional<MediaType> contentType;

	protected Optional<List<MediaType>> accept;

	public UndertowRequestWrapper(HttpServerExchange request) {
		this.request = request;
		this.method = HttpMethod.of(request.getRequestMethod().toString());
		this.path = request.getRequestPath();
	}

	@Override
	public Map<HeaderName, String> getHeaders() {
		if (isNull(headers))
			headers = StreamSupport.stream(request.getRequestHeaders().getHeaderNames().spliterator(), false)
				.map(HttpString::toString)
				.collect(toUnmodifiableMap(HeaderName::of, name -> getHeader(HeaderName.of(name)).get()));
		return headers;
	}

	@Override
	public Optional<String> getHeader(HeaderName name) {
		return Optional.ofNullable(request.getRequestHeaders().getFirst(name.getValue()));
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public Optional<MediaType> getContentType() {
		if (isNull(contentType))
			contentType = getHeader(HeaderName.CONTENT_TYPE)
				.map(MediaType::of);
		return contentType;
	}

	@Override
	public Optional<List<MediaType>> getAccept() {
		if (isNull(accept))
			accept = getHeader(HeaderName.ACCEPT)
				.map(MediaType::list);
		return accept;
	}

	@Override
	public InputStream getInputStream() {
		return request.getInputStream();
	}

	@Override
	public Map<String, String[]> getQueryParams() {
		if (isNull(queryParams))
			queryParams = request.getQueryParameters().entrySet().stream()
				.collect(toMap(Map.Entry::getKey,
						entry -> entry.getValue().toArray(new String[entry.getValue().size()])));

		return queryParams;
	}

	@Override
	public Optional<String[]> getQueryParams(String name) {
		return Optional.ofNullable((String[]) request.getQueryParameters().get(name).toArray());
	}

	@Override
	public HttpMethod getMethod() {
		return method;
	}
}
