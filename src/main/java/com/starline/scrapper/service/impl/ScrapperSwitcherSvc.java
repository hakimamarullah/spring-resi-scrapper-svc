package com.starline.scrapper.service.impl;

import com.starline.scrapper.model.dto.CekResiScrapResponse;
import com.starline.scrapper.model.dto.ScrappingRequest;
import com.starline.scrapper.service.ScrapperService;
import com.starline.scrapper.service.ScrapperSwitcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScrapperSwitcherSvc implements ScrapperSwitcher {

    private final ApplicationContext applicationContext;

    private final ScrapperService<ScrappingRequest, CekResiScrapResponse> scrapperDefault;

    @Override
    public ScrapperService<? extends ScrappingRequest, ?> getScrapperService(String courierCode) {
        log.info("GET scrapper service for: {}", courierCode);
        try {
            return (ScrapperService<? extends ScrappingRequest, ?>) applicationContext.getBean(formatBeanName(courierCode));
        } catch (Exception e) {
            log.warn("Failed to get bean for courierCode {}: {}", courierCode, e.getMessage(), e);
        }


        log.info("Falling back to default scrapper service for: {}", courierCode);
        return scrapperDefault;
    }

    private String formatBeanName(String courierCode) {
        return Optional.ofNullable(courierCode)
                .map(String::toLowerCase)
                .map(String::trim)
                .map(it -> it + "Scrapper")
                .orElse(null);
    }
}
