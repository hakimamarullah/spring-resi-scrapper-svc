package com.starline.scrapper.exceptions;

public class ScrappingFailedException extends ApiException {

    public ScrappingFailedException(String message) {
        super(message);
        this.httpCode = 500;
    }
}
