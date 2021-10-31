package com.ociet.loadgenerator.master;

import com.ociet.loadgenerator.common.Constants;
import com.ociet.loadgenerator.common.LoadRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private KafkaTemplate<String, LoadRequestMessage> kafkaTemplate;

    public void sendMessage(String key, LoadRequestMessage message) {
        logger.info(String.format("Producing message -> %s", message));
        this.kafkaTemplate.send(Constants.LOAD_REQUEST_TOPIC, key, message);
    }
}
