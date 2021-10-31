package com.ociet.loadgenerator.slave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SlaveApplication {
    private static final Logger logger = LoggerFactory.getLogger(SlaveApplication.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        ResultProducer producer = new ResultProducer();
        RequestConsumer consumer = new RequestConsumer(producer);
        logger.info("Slave running");
        consumer.run();
    }
}
