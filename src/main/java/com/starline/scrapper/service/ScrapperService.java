package com.starline.scrapper.service;

import com.starline.scrapper.model.dto.ApiResponse;
import com.starline.scrapper.model.dto.ScrappingRequest;

public interface ScrapperService<T extends ScrappingRequest, V> {

    ApiResponse<V> scrap(T payload) throws InterruptedException;
}
