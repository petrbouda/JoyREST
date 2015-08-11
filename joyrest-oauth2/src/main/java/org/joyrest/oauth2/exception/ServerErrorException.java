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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joyrest.oauth2.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

public class ServerErrorException extends OAuth2Exception {

    private static final long serialVersionUID = -589528744430925746L;

    public ServerErrorException(final String msg, final Throwable t) {
        super(msg, t);
    }

    public String getOAuth2ErrorCode() {
        return "server_error";
    }

    public int getHttpErrorCode() {
        return 500;
    }
}
