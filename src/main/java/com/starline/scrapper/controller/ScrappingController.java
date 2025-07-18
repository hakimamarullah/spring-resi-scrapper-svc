package com.starline.scrapper.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starline.scrapper.annotations.LogRequestResponse;
import com.starline.scrapper.model.dto.ApiResponse;
import com.starline.scrapper.model.dto.CekResiScrapResponse;
import com.starline.scrapper.model.dto.ScrappingRequestEvent;
import com.starline.scrapper.service.ScrapperService;
import com.starline.scrapper.service.ScrapperSwitcher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@RestController
@RequestMapping("/scrapper")
@RequiredArgsConstructor
@LogRequestResponse
public class ScrappingController {

    private final ScrapperSwitcher serviceSwitcher;

    private final ObjectMapper mapper;

    @SuppressWarnings("unchecked")
    @PostMapping(value = "/tracking", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CekResiScrapResponse>> scrap(@RequestBody @Valid ScrappingRequestEvent payload) throws MalformedURLException, InterruptedException {
        ScrapperService<ScrappingRequestEvent, Object> service = (ScrapperService<ScrappingRequestEvent, Object>) serviceSwitcher.getScrapperService(payload.getCourierCode());
        var result = service.scrap(payload);
        ApiResponse<CekResiScrapResponse> response = mapper.convertValue(result, new TypeReference<>() {
        });
        return response.toResponseEntity();

    }
}
