package com.ociet.loadgenerator.master;

import com.ociet.loadgenerator.common.Constants;
import com.ociet.loadgenerator.common.LoadRequestMessage;
import com.ociet.loadgenerator.common.LoadSubrequestMessage;
import com.ociet.loadgenerator.master.dto.LoadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
    private static final int PARTITION_COUNT = 100;

    private final Producer producer;

    @Autowired
    RequestService(Producer producer) {
        this.producer = producer;
    }

    public void requestLoad(LoadRequest loadRequest) {
        LoadSubrequestMessage loadSubrequestMessage = new LoadSubrequestMessage();
        loadSubrequestMessage.setLoopCount(loadRequest.getLoopCount());

        int concurrentInstances = loadRequest.getConcurrentUsers() / Constants.CONCURRENT_USERS_PER_SLAVE;
        String key = String.valueOf(System.currentTimeMillis());

        LoadRequestMessage loadRequestMessage = new LoadRequestMessage(concurrentInstances, loadRequest.getLoopCount());
        this.producer.sendRequest(key, loadRequestMessage);

        for(int i = 0; i < concurrentInstances; i++) {
            loadSubrequestMessage.setRequestOffset(i);
            int partition = Math.round((i / (float) concurrentInstances) * PARTITION_COUNT) + 1;
            partition = Math.min(partition, PARTITION_COUNT - 1);

            this.producer.sendSubrequest(key, partition, loadSubrequestMessage);
        }
    }
}
