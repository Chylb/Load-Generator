package com.ociet.loadgenerator.slave;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.ociet.loadgenerator.common.Constants;
import com.ociet.loadgenerator.common.LoadResultMessage;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResultProducer {
    private static final Logger logger = LoggerFactory.getLogger(ResultProducer.class);
    KafkaProducer<String, String> producer;
    ObjectMapper mapper = new ObjectMapper();

    public ResultProducer() throws IOException {
        InputStream props = Resources.getResource("application.properties").openStream();
        Properties properties = new Properties();
        properties.load(props);
        producer = new KafkaProducer<>(properties);
    }

    public void send(LoadResultMessage message) throws JsonProcessingException {
        logger.info(String.format("Producing message -> %s", message));
        producer.send(new ProducerRecord<String, String>(
                Constants.LOAD_RESULT_TOPIC,
                message.getRequestKey(),
                mapper.writeValueAsString(message)));
        producer.flush();
    }
}
