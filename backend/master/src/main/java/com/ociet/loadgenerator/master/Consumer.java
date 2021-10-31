package com.ociet.loadgenerator.master;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ociet.loadgenerator.common.Constants;
import com.ociet.loadgenerator.common.LoadResultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class Consumer {
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ResultService resultService;

    public Consumer(ResultService resultService) {
        this.resultService = resultService;
    }

    @KafkaListener(topics = Constants.LOAD_RESULT_TOPIC, groupId = "group_id")
    public void consume(String msg) throws IOException {
        logger.info(String.format("Consumed message -> %s", msg));
        LoadResultMessage message = objectMapper.readValue(msg, LoadResultMessage.class);

        resultService.consumeLoadResultMessage(message);
    }
}
