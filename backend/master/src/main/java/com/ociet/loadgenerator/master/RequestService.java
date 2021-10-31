package com.ociet.loadgenerator.master;

import com.ociet.loadgenerator.common.Constants;
import com.ociet.loadgenerator.common.LoadRequestMessage;
import com.ociet.loadgenerator.master.dto.LoadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
    private final Consumer consumer;
    private final Producer producer;
    private final ResultService resultService;

    @Autowired
    RequestService(Consumer consumer, Producer producer, ResultService resultService) {
        this.consumer = consumer;
        this.producer = producer;
        this.resultService = resultService;
    }

    public void requestLoad(LoadRequest loadRequest) {
        LoadRequestMessage loadRequestMessage = new LoadRequestMessage(
                loadRequest.getUrl(),
                loadRequest.getLoopCount()
        );

        int concurrentInstances = loadRequest.getConcurrentUsers() / Constants.CONCURRENT_USERS_PER_SLAVE;
        String key = String.valueOf(System.currentTimeMillis());

        ResultRow resultRow = new ResultRow();
        resultRow.setTotalParts(concurrentInstances);
        resultRow.setLoopCount(loadRequest.getLoopCount());
        resultRow.setUrl(loadRequest.getUrl());
        resultService.addResultEntry(key, resultRow);

        for(int i = 0; i < concurrentInstances; i++) {
            this.producer.sendMessage(key, loadRequestMessage);
        }
    }
}
