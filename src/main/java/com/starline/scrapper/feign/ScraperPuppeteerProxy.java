package com.starline.scrapper.feign;

import com.starline.scrapper.feign.config.FeignBasicConfig;
import com.starline.scrapper.model.dto.proxy.ScrapingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${srv.feign.names.scraper-puppeteer:scraper-puppeteer-svc}", configuration = {FeignBasicConfig.class})
public interface ScraperPuppeteerProxy {

    @GetMapping(value = "/track")
    ScrapingResponse getTrackingInfo(@RequestParam("courier") String courierCode, @RequestParam("resi") String trackingNumber);
}
