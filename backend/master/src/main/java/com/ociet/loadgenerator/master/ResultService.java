package com.ociet.loadgenerator.master;

import com.ociet.loadgenerator.common.Constants;
import com.ociet.loadgenerator.common.LoadResultMessage;
import com.ociet.loadgenerator.master.dto.LoadResult;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class ResultService {
    private HashMap<String, ResultRow> resultMap = new HashMap<>();

    public void consumeLoadResultMessage(LoadResultMessage message) {
        ResultRow resultRow = resultMap.get(message.getRequestKey());
        resultRow.responseTimeSum += message.getResponseTimeSum();
        resultRow.setMaxResponseTime(Math.max(resultRow.maxResponseTime, message.getMaxResponseTime()));
        resultRow.setReceivedParts(resultRow.getReceivedParts() + 1);
        resultMap.put(message.getRequestKey(), resultRow);
    }

    public void addResultEntry(String key, ResultRow entry) {
        resultMap.put(key, entry);
    }

    public Collection<LoadResult> getResults() {
        return resultMap.entrySet().stream().map(e -> {
            ResultRow v = e.getValue();
            return new LoadResult(
                    e.getKey(),
                    v.getUrl(),
                    v.getTotalParts() * Constants.CONCURRENT_USERS_PER_SLAVE,
                    v.getResponseTimeSum() / Constants.CONCURRENT_USERS_PER_SLAVE / v.getLoopCount() / v.getTotalParts(),
                    v.getMaxResponseTime(),
                    v.getReceivedParts() == v.getTotalParts() ? "finished" : "unfinished");
        }).collect(Collectors.toList());
    }
}
