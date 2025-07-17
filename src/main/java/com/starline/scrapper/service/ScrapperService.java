package com.starline.scrapper.service;

import com.starline.scrapper.model.dto.ApiResponse;
import com.starline.scrapper.model.dto.ScrappingRequestEvent;

import java.net.MalformedURLException;

public interface ScrapperService<T extends ScrappingRequestEvent, V> {

    ApiResponse<V> scrap(T payload) throws InterruptedException, MalformedURLException;
}
