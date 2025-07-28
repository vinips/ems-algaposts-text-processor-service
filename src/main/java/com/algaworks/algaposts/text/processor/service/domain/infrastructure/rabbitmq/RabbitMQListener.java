package com.algaworks.algaposts.text.processor.service.domain.infrastructure.rabbitmq;

import com.algaworks.algaposts.text.processor.service.domain.model.PostProcessingData;
import com.algaworks.algaposts.text.processor.service.domain.service.TextProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQListener {

    private final TextProcessorService textProcessorService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_POST_PROCESSING, concurrency = "2-3")
    public void handlePostProcessing(@Payload PostProcessingData postProcessingData) {
        textProcessorService.processWordCountAndValue(postProcessingData);

    }

}
