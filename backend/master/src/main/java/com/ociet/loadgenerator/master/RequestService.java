package com.ociet.loadgenerator.master;

import com.ociet.loadgenerator.common.Constants;
import com.ociet.loadgenerator.common.LoadRequestMessage;
import com.ociet.loadgenerator.master.dto.LoadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class RequestService {
    private static final int PARTITION_COUNT = 100;

    private final Producer producer;
    private final ResultService resultService;

    @Autowired
    RequestService(Producer producer, ResultService resultService) {
        this.producer = producer;
        this.resultService = resultService;
    }

    public void requestLoad(LoadRequest loadRequest) {
        LoadRequestMessage loadRequestMessage = new LoadRequestMessage();
        loadRequestMessage.setLoopCount(loadRequest.getLoopCount());

        int concurrentInstances = loadRequest.getConcurrentUsers() / Constants.CONCURRENT_USERS_PER_SLAVE;
        String key = String.valueOf(System.currentTimeMillis());

        ResultRow resultRow = new ResultRow();
        resultRow.setTotalParts(concurrentInstances);
        resultRow.setLoopCount(loadRequest.getLoopCount());
        resultRow.setErrors(new LinkedList<>());
        resultService.addResultEntry(key, resultRow);

        for(int i = 0; i < concurrentInstances; i++) {
            loadRequestMessage.setRequestOffset(i);
            int partition = Math.round((i / (float) concurrentInstances) * PARTITION_COUNT) + 1;
            partition = Math.min(partition, PARTITION_COUNT - 1);

            this.producer.sendMessage(key, partition, loadRequestMessage);
        }
    }
}
