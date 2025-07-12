package com.starline.scrapper.service;

import com.starline.scrapper.model.dto.ApiResponse;
import com.starline.scrapper.model.dto.ScrappingRequest;

import java.net.MalformedURLException;
import java.nio.charset.MalformedInputException;

public interface ScrapperService<T extends ScrappingRequest, V> {

    ApiResponse<V> scrap(T payload) throws InterruptedException, MalformedURLException, MalformedInputException;
}
