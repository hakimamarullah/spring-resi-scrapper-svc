package com.starline.scrapper.service;

import com.starline.scrapper.model.dto.ScrappingRequest;

public interface ScrapperSwitcher {

    ScrapperService<? extends ScrappingRequest, ?> getScrapperService(String courierCode);
}
