package com.starline.scrapper.exceptions;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

@RegisterReflectionForBinding({DataNotFoundException.class})
public class DataNotFoundException extends ApiException {

    public DataNotFoundException(String message) {
        super(message);
        setHttpCode(404);
    }
}
