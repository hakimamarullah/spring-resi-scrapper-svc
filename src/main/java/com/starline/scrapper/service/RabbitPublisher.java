package com.starline.scrapper.service;

import com.starline.scrapper.model.dto.event.ScrappingResultEvent;

public interface RabbitPublisher {

    void publish(String exchange, String routingKey, Object event);

    void publishScrappingResult(ScrappingResultEvent event);
}
