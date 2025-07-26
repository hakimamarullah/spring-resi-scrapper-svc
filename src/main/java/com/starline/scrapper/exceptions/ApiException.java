package com.starline.scrapper.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

@Setter
@Getter
@RegisterReflectionForBinding({ApiException.class})
public class ApiException extends RuntimeException {

    protected int httpCode;

    public ApiException(String message) {
        super(message);
        this.httpCode = 500;
    }

}
