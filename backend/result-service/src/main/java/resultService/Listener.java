package resultService;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ociet.loadgenerator.common.Constants;
import com.ociet.loadgenerator.common.LoadRequestMessage;
import com.ociet.loadgenerator.common.LoadResultMessage;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import jakarta.inject.Inject;

@KafkaListener(offsetReset = OffsetReset.EARLIEST, uniqueGroupId = true)
class Listener {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    Db db;

    @Topic(Constants.LOAD_RESULT_TOPIC)
    public void receiveResult(String msg) throws JsonProcessingException {
        LoadResultMessage loadResultMessage = objectMapper.readValue(msg, LoadResultMessage.class);
        db.add(loadResultMessage);
    }

    @Topic(Constants.LOAD_REQUEST_TOPIC)
    public void receiveRequest(@KafkaKey String key, String msg) throws JsonProcessingException {
        LoadRequestMessage loadRequestMessage = objectMapper.readValue(msg, LoadRequestMessage.class);
        db.add(key, loadRequestMessage);
    }
}