package com.starline.scrapper.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starline.scrapper.feign.ScraperPuppeteerProxy;
import com.starline.scrapper.model.dto.ApiResponse;
import com.starline.scrapper.model.dto.CekResiScrapResponse;
import com.starline.scrapper.model.dto.ScrappingRequestEvent;
import com.starline.scrapper.model.dto.proxy.AWBInfo;
import com.starline.scrapper.model.dto.proxy.ScrapingResponse;
import com.starline.scrapper.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("puppeteerScraper")
@RequiredArgsConstructor
public class AWBScraperPuppeteer implements ScrapperService<ScrappingRequestEvent, CekResiScrapResponse> {

    private final ScraperPuppeteerProxy scraperPuppeteerProxy;

    private final ObjectMapper mapper;

    @Override
    public ApiResponse<CekResiScrapResponse> scrap(ScrappingRequestEvent payload) {
        ScrapingResponse res = scraperPuppeteerProxy.getTrackingInfo(payload.getCourierCode(), payload.getTrackingNumber());

        List<AWBInfo> awbInfos = res.getAwbInfos();
        if (awbInfos.size() >= 2) {
            var firstRecord = awbInfos.getFirst();
            var secondRecord = awbInfos.getLast();

            var firstTimestamp = firstRecord.getDate();
            var secondTimestamp = secondRecord.getDate();

            var firstCheckpoint = firstRecord.getStatus();
            var secondCheckpoint = secondRecord.getStatus();

            var lastIsLatest = Optional.ofNullable(secondTimestamp)
                    .map(it -> it.compareTo(firstTimestamp) >= 0)
                    .orElse(false);

            return ApiResponse.setSuccess(CekResiScrapResponse.builder()
                    .timestamp(Boolean.TRUE.equals(lastIsLatest) ? secondTimestamp : firstTimestamp)
                    .checkpoint(Boolean.TRUE.equals(lastIsLatest) ? secondCheckpoint : firstCheckpoint)
                    .build());
        }
        return ApiResponse.setResponse(CekResiScrapResponse.builder().build(), "No data found", 200);

    }
}
