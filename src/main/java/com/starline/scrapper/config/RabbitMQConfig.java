package com.starline.scrapper.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQConfig {

    // Exchange names
    public static final String SCRAPPING_EXCHANGE = "scrapping.exchange";


    // Queue names
    public static final String SCRAPPING_REQUEST_QUEUE = "scrapping.request.queue";


    public static final String SCRAPPING_DONE_QUEUE = "scrapping.done.queue";


    // Routing keys
    public static final String SCRAPPING_DONE_ROUTING_KEY = "scrapping.done";
    public static final String SCRAPPING_REQUEST_ROUTING_KEY = "scrapping.request";


    // Exchanges
    @Bean
    public TopicExchange scrappingExchange() {
        return new TopicExchange(SCRAPPING_EXCHANGE);
    }



    @Bean
    public Queue scrappingDoneQueue() {
        return QueueBuilder.durable(SCRAPPING_DONE_QUEUE)
                .withArgument("x-message-ttl", 300000) // 5 minutes TTL
                .build();
    }

    @Bean
    public Queue scrappingRequestQueue() {
        return QueueBuilder.durable(SCRAPPING_REQUEST_QUEUE)
                .withArgument("x-message-ttl", 300000) // 5 minutes TTL
                .build();
    }


    // Bindings
    @Bean
    public Binding scrappingDoneBinding() {
        return BindingBuilder
                .bind(scrappingDoneQueue())
                .to(scrappingExchange())
                .with(SCRAPPING_DONE_ROUTING_KEY);
    }

    @Bean
    public Binding scrappingRequestBinding() {
        return BindingBuilder
                .bind(scrappingRequestQueue())
                .to(scrappingExchange())
                .with(SCRAPPING_REQUEST_ROUTING_KEY);
    }



    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}

