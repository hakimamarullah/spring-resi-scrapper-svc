package com.starline.scrapper.controller;

import com.starline.scrapper.annotations.LogRequestResponse;
import com.starline.scrapper.model.dto.ApiResponse;
import com.starline.scrapper.model.dto.CekResiScrapResponse;
import com.starline.scrapper.model.dto.ScrappingRequest;
import com.starline.scrapper.service.ScrapperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scrapper")
@RequiredArgsConstructor
@LogRequestResponse
public class ScrappingController {

    private final ScrapperService<ScrappingRequest, CekResiScrapResponse> scrapperService;

    @Qualifier("jneScrapper")
    private final ScrapperService<ScrappingRequest, CekResiScrapResponse> jneScrapperService;

    @PostMapping(value = "/tracking", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CekResiScrapResponse>> scrap(@RequestBody @Valid ScrappingRequest payload) throws InterruptedException {
        if (payload.getCourierCode().equalsIgnoreCase("jne")) {
            return jneScrapperService.scrap(payload).toResponseEntity();
        }
        return scrapperService.scrap(payload).toResponseEntity();
    }
}
