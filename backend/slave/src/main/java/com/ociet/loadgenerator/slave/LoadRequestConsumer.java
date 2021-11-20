package com.ociet.loadgenerator.slave;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ociet.loadgenerator.common.Constants;
import com.ociet.loadgenerator.common.LoadSubrequestMessage;
import com.ociet.loadgenerator.common.LoadResultMessage;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class LoadRequestConsumer {
    private static final Logger logger = LoggerFactory.getLogger(LoadRequestConsumer.class);
    private static final long MAX_CONSUME_TIME_DELAY = 1000;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Constants.CONCURRENT_USERS_PER_SLAVE);

    @Autowired
    private LoadResultProducer producer;

    private final ThrowingFunction<HttpRequestGeneratorArguments, HttpRequest> requestGenerator = (args) -> {
        //throw new Exception("exception exception serious very" + args.getUserOffset());
        return HttpRequest.newBuilder(
                        URI.create("http://localhost:1111/players/" + (args.getUserOffset() % 18278))
                )
                .build();
    };

    @KafkaListener(topics = Constants.LOAD_SUBREQUEST_TOPIC, groupId = "group_id")
    public void consume(ConsumerRecord<String, String> record) throws IOException, InterruptedException {
        logger.info(String.format("Consumed message -> %s", record.key()));
        LoadSubrequestMessage message = objectMapper.readValue(record.value(), LoadSubrequestMessage.class);

        CountDownLatch latch = new CountDownLatch(Constants.CONCURRENT_USERS_PER_SLAVE);

        long startTimestamp = System.currentTimeMillis();
        AtomicLong responseTimeSum = new AtomicLong();
        AtomicLong maxResponseTime = new AtomicLong();
        AtomicReference<String> error = new AtomicReference<>(null);

        if (Math.abs(record.timestamp() - startTimestamp) > MAX_CONSUME_TIME_DELAY) {
            error.set("Exceeded max consume time delay");
        }

        if (error.get() == null) {
            for (int i = 0; i < Constants.CONCURRENT_USERS_PER_SLAVE; i++) {
                var requestGeneratorArguments = new HttpRequestGeneratorArguments();
                requestGeneratorArguments.setUserOffset(message.getRequestOffset() * Constants.CONCURRENT_USERS_PER_SLAVE + i);

                executor.execute(() -> {
                    HttpClient client = HttpClient.newHttpClient();

                    long userResponseTimeSum = 0;
                    long userMaxResponseTime = 0;

                    try {
                        for (int j = 0; j < message.getLoopCount(); j++) {
                            var httpRequest = requestGenerator.apply(requestGeneratorArguments);

                            long start = System.nanoTime();
                            var response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                            long responseTime = System.nanoTime() - start;

                            userResponseTimeSum += responseTime;
                            userMaxResponseTime = Math.max(userMaxResponseTime, responseTime);

                            requestGeneratorArguments.setLoopIteration(j);
                            requestGeneratorArguments.setPreviousResponse(response);
                        }
                    } catch (Exception e) {
                        String exceptionMessage = e.getMessage();
                        if(exceptionMessage == null) {
                            exceptionMessage = e.getClass().getName();
                        }

                        logger.error(exceptionMessage);
                        error.set(exceptionMessage);
                    }
                    responseTimeSum.addAndGet(userResponseTimeSum);
                    long finalUserMaxResponseTime = userMaxResponseTime;
                    maxResponseTime.updateAndGet(x -> Math.max(x, finalUserMaxResponseTime));

                    latch.countDown();
                });
            }
            latch.await();
        }

        LoadResultMessage resultMessage = new LoadResultMessage(
                record.key(),
                responseTimeSum.get(),
                maxResponseTime.get(),
                startTimestamp,
                error.get()
        );
        producer.send(resultMessage);
    }

    @Getter
    @Setter
    public static class HttpRequestGeneratorArguments {
        private int userOffset;
        private int loopIteration;
        HttpResponse previousResponse;
    }
}
