package org.joyrest.aspect;

import org.joyrest.model.request.InternalRequest;
import org.joyrest.model.response.InternalResponse;
import org.joyrest.routing.EntityRoute;
import org.joyrest.routing.Route;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class AspectChainImpl<REQ, RESP> implements AspectChain<REQ, RESP> {

	private final Queue<Aspect<REQ, RESP>> aspects = new ArrayDeque<>();

	private final EntityRoute<REQ, RESP> route;

	public AspectChainImpl(EntityRoute<REQ, RESP> route) {
		requireNonNull(route);
		this.route = route;
		this.aspects.addAll(route.getAspects());
	}

	@Override
	public InternalResponse<RESP> proceed(InternalRequest<REQ> request, InternalResponse<RESP> response) {
		requireNonNull(request);
		Aspect<REQ, RESP> aspect = aspects.poll();

		if (nonNull(aspect))
			return aspect.around(this, request, response);

		return route.execute(request, response);
	}

	@Override
	public EntityRoute<REQ, RESP> getRoute() {
		return route;
	}
}