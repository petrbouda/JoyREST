package org.joyrest.routing.matcher;

import static org.joyrest.model.http.MediaType.*;
import static org.junit.Assert.assertTrue;

import org.joyrest.model.http.HttpMethod;
import org.joyrest.model.http.HttpStatus;
import org.joyrest.model.request.Request;
import org.joyrest.model.response.Response;
import org.joyrest.routing.InternalRoute;
import org.joyrest.routing.RouteAction;
import org.joyrest.stubs.RequestStub;
import org.junit.Test;

public class ConsumesMatcherTest {

	@Test
	public void consumes_match_route_wildcard() throws Exception {
		InternalRoute route = basicRoute();
		route.consumes(WILDCARD);

		RequestStub req = new RequestStub();

		boolean result = RequestMatcher.matchConsumes(route, req);
		assertTrue(result);
	}

	@Test
	public void consumes_match_route_json() throws Exception {
		InternalRoute route = basicRoute();
		route.consumes(JSON);

		RequestStub req = new RequestStub();
		req.setContentType(JSON);

		boolean result = RequestMatcher.matchConsumes(route, req);
		assertTrue(result);
	}

	@Test
	public void consumes_match_route_multiple() throws Exception {
		InternalRoute route = basicRoute();
		route.consumes(JSON, XML, PLAIN_TEXT);

		RequestStub req = new RequestStub();
		req.setContentType(XML);

		boolean result = RequestMatcher.matchConsumes(route, req);
		assertTrue(result);
	}

	private static InternalRoute basicRoute() {
		RouteAction<Request<?>, Response<?>> action =
			(req, resp) -> resp.status(HttpStatus.CONFLICT);

		return new InternalRoute("/path", HttpMethod.POST, action, null, null);
	}
}