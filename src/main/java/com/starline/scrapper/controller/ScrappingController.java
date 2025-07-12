package com.starline.scrapper.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starline.scrapper.annotations.LogRequestResponse;
import com.starline.scrapper.model.dto.ApiResponse;
import com.starline.scrapper.model.dto.CekResiScrapResponse;
import com.starline.scrapper.model.dto.ScrappingRequest;
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
import java.nio.charset.MalformedInputException;

@RestController
@RequestMapping("/scrapper")
@RequiredArgsConstructor
@LogRequestResponse
public class ScrappingController {

    private final ScrapperSwitcher serviceSwitcher;

    private final ObjectMapper mapper;

    @SuppressWarnings("unchecked")
    @PostMapping(value = "/tracking", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CekResiScrapResponse>> scrap(@RequestBody @Valid ScrappingRequest payload) throws InterruptedException, MalformedURLException, MalformedInputException {
        ScrapperService<ScrappingRequest, Object> service = (ScrapperService<ScrappingRequest, Object>) serviceSwitcher.getScrapperService(payload.getCourierCode());
        ApiResponse<CekResiScrapResponse> response = mapper.convertValue(service.scrap(payload), new TypeReference<>() {
        });
        return response.toResponseEntity();

    }
}
