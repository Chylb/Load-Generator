package com.ociet.loadgenerator.slave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

public class SlaveApplication {
    private static final Logger logger = LoggerFactory.getLogger(SlaveApplication.class);

    public static void main(String[] applicationArgs) throws IOException, InterruptedException {
        ResultProducer producer = new ResultProducer();
        RequestConsumer consumer = new RequestConsumer(producer);

        logger.info("Slave running");

        consumer.run((args) -> {
            return HttpRequest.newBuilder(
                            URI.create("http://localhost:1111/players/" + (args.getUserOffset() % 18278))
                    )
                    .build();
        });
    }
}
