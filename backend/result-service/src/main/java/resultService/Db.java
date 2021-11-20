package resultService;

import com.ociet.loadgenerator.common.LoadRequestMessage;
import com.ociet.loadgenerator.common.LoadResultMessage;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Singleton
public class Db {
    private final Map<String, Record> records = new HashMap<>();

    public void add(LoadResultMessage resultMsg) {
        Record record = records.get(resultMsg.getRequestKey());
        if(record == null) {
            record = new Record();
        }

        record.key = Long.parseLong(resultMsg.getRequestKey());
        record.responseTimeSum += resultMsg.getResponseTimeSum();
        record.maxResponseTime = Math.max(record.maxResponseTime, resultMsg.getMaxResponseTime());
        record.receivedParts++;
        if(resultMsg.getError() != null) {
            record.errors.add(resultMsg.getError());
        }

        records.put(resultMsg.getRequestKey(), record);
    }

    public void add(String key, LoadRequestMessage requestMsg) {
        Record record = records.get(key);
        if(record == null) {
            record = new Record();
        }
        record.key = Long.parseLong(key);
        record.totalParts = requestMsg.getTotalParts();
        record.loopCount = requestMsg.getLoopCount();

        records.put(key, record);
    }

    public Collection<Record> getAll() {
        return records.values();
    }

    @Setter
    @Getter
    public static class Record {
        private long key;
        private long responseTimeSum;
        private long maxResponseTime;
        private int receivedParts;
        private int totalParts = 1;
        private int loopCount = 1;
        private Set<String> errors = new LinkedHashSet<>();
    }
}
