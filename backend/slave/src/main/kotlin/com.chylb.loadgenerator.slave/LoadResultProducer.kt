package com.chylb.loadgenerator.slave

import com.chylb.loadgenerator.common.Constants
import com.chylb.loadgenerator.common.LoadResultMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class LoadResultProducer {
    companion object {
        private val logger = LoggerFactory.getLogger(LoadResultProducer::class.java)
    }

    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, LoadResultMessage?>? = null

    fun send(message: LoadResultMessage?) {
        logger.info(String.format("Producing message -> %s", message))
        kafkaTemplate!!.send(Constants.LOAD_RESULT_TOPIC, message)
    }
}