package org.joyrest.model.request;

import org.joyrest.model.http.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ImmutableRequest implements Request {

    private final Request request;

    private ImmutableRequest(Request request) {
        this.request = request;
    }

    public static final ImmutableRequest of(Request request) {
        return new ImmutableRequest(request);
    }

    @Override
    public Optional<String> getHeader(HeaderName name) {
        return request.getHeader(name);
    }

    @Override
    public Map<HeaderName, String> getHeaderNames() {
        return request.getHeaderNames();
    }

    @Override
    public Map<String, PathParam> getPathParams() {
        return request.getPathParams();
    }

    @Override
    public String getPathParam(String name) {
        return request.getPathParam(name);
    }

    @Override
    public Map<String, String[]> getQueryParams() {
        return request.getQueryParams();
    }

    @Override
    public HttpMethod getMethod() {
        return request.getMethod();
    }

    @Override
    public String getPath() {
        return request.getPath();
    }

    @Override
    public List<String> getPathParts() {
        return request.getPathParts();
    }

    @Override
    public Optional<Object> getEntity() {
        return request.getEntity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(request);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ImmutableRequest other = (ImmutableRequest) obj;
        return Objects.equals(this.request, other.request);
    }
}