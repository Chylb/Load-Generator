package resultService

import com.chylb.loadgenerator.common.Constants
import com.chylb.loadgenerator.common.LoadRequestMessage
import com.chylb.loadgenerator.common.LoadResultMessage
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic
import javax.inject.Inject


@KafkaListener(offsetReset = OffsetReset.EARLIEST, uniqueGroupId = true)
internal class Listener {
    private val objectMapper = ObjectMapper()

    @Inject
    var db: Db? = null
    @Topic(Constants.LOAD_RESULT_TOPIC)
    @Throws(JsonProcessingException::class)
    fun receiveResult(msg: String?) {
        val loadResultMessage =
            objectMapper.readValue(msg, LoadResultMessage::class.java)
        db?.add(loadResultMessage)
    }

    @Topic(Constants.LOAD_REQUEST_TOPIC)
    @Throws(JsonProcessingException::class)
    fun receiveRequest(@KafkaKey key: String, msg: String?) {
        val loadRequestMessage =
            objectMapper.readValue(msg, LoadRequestMessage::class.java)
        db?.add(key, loadRequestMessage)
    }
}