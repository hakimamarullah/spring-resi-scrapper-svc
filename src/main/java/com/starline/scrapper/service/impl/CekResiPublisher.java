package com.starline.scrapper.service.impl;

import com.starline.scrapper.config.RabbitMQConfig;
import com.starline.scrapper.model.dto.event.ScrappingResultEvent;
import com.starline.scrapper.service.RabbitPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@RegisterReflectionForBinding({
        ScrappingResultEvent.class
})
public class CekResiPublisher implements RabbitPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(String exchange, String routingKey, Object event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);

    }

    @Override
    public void publishScrappingResult(ScrappingResultEvent event) {
        publish(RabbitMQConfig.SCRAPPING_EXCHANGE, RabbitMQConfig.SCRAPPING_DONE_ROUTING_KEY, event);
    }
}
