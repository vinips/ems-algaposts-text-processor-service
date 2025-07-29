package com.algaworks.algaposts.text.processor.service.infrastructure.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    private static final String POST_PROCESSING = "text-processor-service.post-processing.v1";

    public static final String DIRECT_EXCHANGE_POST_PROCESSING = POST_PROCESSING + ".e";
    public static final String QUEUE_POST_PROCESSING = POST_PROCESSING + ".q";
    public static final String DEAD_LETTER_QUEUE_POST_PROCESSING = POST_PROCESSING + ".dlq";

    public static final String ROUTING_KEY_PROCESS_TEXT = "process-text";

    //exchange and routing key to post-service
    public static final String DIRECT_EXCHANGE_POST_PROCESSING_RESULT = "post-service.post-processing-result.v1.e";
    public static final String ROUTING_KEY_PROCESSED_TEXT = "processed-text";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Queue queuePostProcessing(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "");
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUE_POST_PROCESSING);

        return QueueBuilder
                .durable(QUEUE_POST_PROCESSING)
                .withArguments(args)
                .build();
    }

    @Bean
    public Queue deadLetterQueuePostProcessing() {
        return QueueBuilder
                .durable(DEAD_LETTER_QUEUE_POST_PROCESSING)
                .build();
    }

    @Bean
    public DirectExchange exchangePostProcessing() {
        return ExchangeBuilder
                .directExchange(DIRECT_EXCHANGE_POST_PROCESSING)
                .build();
    }

    @Bean
    public Binding bindingPostProcessing() {
        return BindingBuilder
                .bind(queuePostProcessing())
                .to(exchangePostProcessing())
                .with(ROUTING_KEY_PROCESS_TEXT);
    }




}
