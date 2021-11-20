package com.ociet.loadgenerator.master;

import com.ociet.loadgenerator.common.Constants;
import com.ociet.loadgenerator.common.LoadRequestMessage;
import com.ociet.loadgenerator.common.LoadSubrequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private KafkaTemplate<String, LoadSubrequestMessage> kafkaSubrequestTemplate;

    @Autowired
    private KafkaTemplate<String, LoadRequestMessage> kafkaRequestTemplate;

    public void sendSubrequest(String key, int partition, LoadSubrequestMessage message) {
        logger.info(String.format("Producing message -> %s", message));
        this.kafkaSubrequestTemplate.send(Constants.LOAD_SUBREQUEST_TOPIC, partition, key, message);
    }

    public void sendRequest(String key, LoadRequestMessage message) {
        logger.info(String.format("Producing message -> %s", message));
        this.kafkaRequestTemplate.send(Constants.LOAD_REQUEST_TOPIC, key, message);
    }
}
