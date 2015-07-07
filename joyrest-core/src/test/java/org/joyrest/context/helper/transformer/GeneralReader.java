package org.joyrest.context.helper.transformer;

import org.joyrest.model.http.MediaType;
import org.joyrest.model.request.InternalRequest;
import org.joyrest.routing.InternalRoute;
import org.joyrest.routing.entity.Type;
import org.joyrest.transform.Reader;

public class GeneralReader implements Reader {

    @Override
    public <T> T readFrom(InternalRequest<Object> request, Type<T> clazz) {
        return null;
    }

    @Override
    public boolean isReaderCompatible(InternalRoute route) {
        return route.getRequestType().getType() == String.class;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.of("reader/GENERAL");
    }

    @Override
    public boolean isClassCompatible(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean isGeneral() {
        return true;
    }
}
