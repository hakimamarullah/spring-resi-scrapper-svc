package com.starline.scrapper.service;

import com.starline.scrapper.model.dto.ScrappingRequestEvent;

public interface ScrapperSwitcher {

    ScrapperService<? extends ScrappingRequestEvent, ?> getScrapperService(String courierCode);
}
