package com.starline.scrapper.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starline.scrapper.config.RabbitMQConfig;
import com.starline.scrapper.exceptions.ScrappingFailedException;
import com.starline.scrapper.model.dto.ApiResponse;
import com.starline.scrapper.model.dto.CekResiScrapResponse;
import com.starline.scrapper.model.dto.ScrappingRequestEvent;
import com.starline.scrapper.model.dto.event.ScrappingResultEvent;
import com.starline.scrapper.service.RabbitPublisher;
import com.starline.scrapper.service.ScrapperService;
import com.starline.scrapper.service.ScrapperSwitcher;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScrappingListener {

    private final ScrapperSwitcher scrapperSwitcher;

    private final RabbitPublisher rabbitPublisher;

    private final ObjectMapper mapper;

    @SuppressWarnings("unchecked")
    @WithSpan
    @RabbitListener(queues = {RabbitMQConfig.SCRAPPING_REQUEST_QUEUE})
    public void onMessage(@Valid ScrappingRequestEvent payload) throws MalformedURLException, InterruptedException, JsonProcessingException {
        log.info("Received scrapping request: {} {}", payload.getTrackingNumber(), mapper.writeValueAsString(payload));
        try {
            var service = (ScrapperService<ScrappingRequestEvent, Object>) scrapperSwitcher.getScrapperService(payload.getCourierCode());
            ApiResponse<CekResiScrapResponse> result = mapper.convertValue(service.scrap(payload), new TypeReference<>() {
            });
            if (!result.is2xxSuccessful()) {
                throw new ScrappingFailedException(result.getMessage());
            }
            var data = result.getData();
            var scrappingDoneEvent = ScrappingResultEvent.builder()
                    .trackingNumber(payload.getTrackingNumber())
                    .courierCode(payload.getCourierCode())
                    .timestamp(data.getTimestamp())
                    .checkpoint(data.getCheckpoint())
                    .type(payload.getType())
                    .additionalValue1(payload.getAdditionalValue1())
                    .userId(payload.getUserId())
                    .phoneLast5(payload.getPhoneLast5())
                    .build();
            log.info("Publishing scrapping result: {}", mapper.writeValueAsString(scrappingDoneEvent));
            rabbitPublisher.publishScrappingResult(scrappingDoneEvent);
        } catch (Exception e) {
            log.error("FAILED SCRAPPING FOR: {} REASON: {}", payload.getTrackingNumber(), e.getMessage());
            throw e;
        }
        log.info("Finished scrapping request: {} COURIER: {}", payload.getTrackingNumber(), payload.getCourierCode());
    }
}
