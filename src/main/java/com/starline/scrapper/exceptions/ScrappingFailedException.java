package com.starline.scrapper.exceptions;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

@RegisterReflectionForBinding({ScrappingFailedException.class})
public class ScrappingFailedException extends ApiException {

    public ScrappingFailedException(String message) {
        super(message);
        setHttpCode(500);
    }
}
