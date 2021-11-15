package com.ociet.loadgenerator.slave;

import com.ociet.loadgenerator.common.Constants;
import com.ociet.loadgenerator.common.LoadResultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoadResultProducer {
    private static final Logger logger = LoggerFactory.getLogger(LoadResultProducer.class);

    @Autowired
    private KafkaTemplate<String, LoadResultMessage> kafkaTemplate;

    public void send(LoadResultMessage message) {
        logger.info(String.format("Producing message -> %s", message));
        this.kafkaTemplate.send(Constants.LOAD_RESULT_TOPIC, message);
    }
}
