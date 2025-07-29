package com.algaworks.algaposts.text.processor.service.domain.service;

import com.algaworks.algaposts.text.processor.service.infrastructure.rabbitmq.RabbitMQConfig;
import com.algaworks.algaposts.text.processor.service.domain.model.PostProcessingData;
import com.algaworks.algaposts.text.processor.service.domain.model.PostProcessingResultData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.StringTokenizer;

@Service
@Slf4j
@RequiredArgsConstructor
public class TextProcessorService {

    private final RabbitTemplate rabbitTemplate;

    private static final Double WORD_PRICE = 0.10;

    public void processWordCountAndValue(PostProcessingData postProcessingData) {
        Integer wordCount = getWordCount(postProcessingData.getPostBody());
        Double textPrice = calculeTextPrice(wordCount);

        log.info("Words count: {}, Text price: {}", wordCount, textPrice);

        PostProcessingResultData resultData = creatPostProcessingResultData(postProcessingData, wordCount, textPrice);

        sendPostProcessingResultData(resultData);
    }

    private Integer getWordCount(String postBody) {
        return new StringTokenizer(postBody).countTokens();
    }

    private Double calculeTextPrice(Integer wordCount) {
        return wordCount * WORD_PRICE;
    }

    private PostProcessingResultData creatPostProcessingResultData(PostProcessingData postProcessingData,
                                                                   Integer wordCount, Double textPrice) {
        return PostProcessingResultData.builder()
                .postId(postProcessingData.getPostId())
                .wordCount(wordCount)
                .calculatedValue(textPrice)
                .build();
    }

    private void sendPostProcessingResultData(PostProcessingResultData resultData) {
        String exchange = RabbitMQConfig.DIRECT_EXCHANGE_POST_PROCESSING_RESULT;
        String rountingKey = RabbitMQConfig.ROUTING_KEY_PROCESSED_TEXT;
        Object payLoad = resultData;

        rabbitTemplate.convertAndSend(exchange, rountingKey, payLoad);
    }
}
