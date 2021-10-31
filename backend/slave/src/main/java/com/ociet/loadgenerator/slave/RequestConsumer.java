package com.ociet.loadgenerator.slave;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.ociet.loadgenerator.common.Constants;
import com.ociet.loadgenerator.common.LoadRequestMessage;
import com.ociet.loadgenerator.common.LoadResultMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

public class RequestConsumer {
    private static final Logger logger = LoggerFactory.getLogger(SlaveApplication.class);
    private final ObjectMapper mapper = new ObjectMapper();
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Constants.CONCURRENT_USERS_PER_SLAVE);
    private final ResultProducer producer;
    private final KafkaConsumer<String, String> consumer;

    public RequestConsumer(ResultProducer producer) throws IOException {
        InputStream props = Resources.getResource("application.properties").openStream();
        Properties properties = new Properties();
        properties.load(props);
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(List.of(Constants.LOAD_REQUEST_TOPIC));

        this.producer = producer;
    }

    public void run() throws JsonProcessingException, InterruptedException {
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records) {
                logger.info("Processing request " + record.key());

                LoadRequestMessage message = mapper.readValue(record.value(), LoadRequestMessage.class);
                CountDownLatch latch = new CountDownLatch(Constants.CONCURRENT_USERS_PER_SLAVE);

                long startTimestamp = System.currentTimeMillis();
                AtomicLong responseTimeSum = new AtomicLong();
                AtomicLong maxResponseTime = new AtomicLong();

                for (int i = 0; i < Constants.CONCURRENT_USERS_PER_SLAVE; i++) {
                    executor.execute(() -> {
                        long userResponseTimeSum = 0;
                        long userMaxResponseTime = 0;
                        for (int j = 0; j < message.getLoopCount(); j++) {
                            try {
                                long start = System.nanoTime();
                                HttpRequester.makeRequest(message.getUrl());
                                long responseTime = System.nanoTime() - start;

                                userResponseTimeSum += responseTime;
                                userMaxResponseTime = Math.max(userMaxResponseTime, responseTime);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        responseTimeSum.addAndGet(userResponseTimeSum);
                        long finalUserMaxResponseTime = userMaxResponseTime;
                        maxResponseTime.updateAndGet(x -> Math.max(x, finalUserMaxResponseTime));

                        latch.countDown();
                    });
                }
                latch.await();

                LoadResultMessage resultMessage = new LoadResultMessage(
                        record.key(),
                        responseTimeSum.get(),
                        maxResponseTime.get(),
                        startTimestamp
                );
                producer.send(resultMessage);
            }
        }
    }
}
