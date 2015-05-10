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
package org.joyrest.routing;

import org.joyrest.model.request.Request;
import org.joyrest.model.response.Response;

/**
 * Class that represents action which is execute when the given route is resolved.
 *
 * @param <REQ> represents type of the request object
 * @param <RESP> represents type of the response object
 *
 * @see Route
 * @author pbouda
 */
public interface RouteAction<REQ, RESP> {

	/**
	 * Performs this route action
	 *
	 * @param request injected request into the route action
	 * @param response injected response into the route action
	 */
	void perform(Request<REQ> request, Response<RESP> response);

}