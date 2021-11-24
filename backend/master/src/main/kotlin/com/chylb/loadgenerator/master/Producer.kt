package com.chylb.loadgenerator.master

import com.chylb.loadgenerator.common.Constants
import com.chylb.loadgenerator.common.LoadRequestMessage
import com.chylb.loadgenerator.common.LoadSubrequestMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service


@Service
class Producer {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Producer::class.java)
    }

    @Autowired
    private val kafkaSubrequestTemplate: KafkaTemplate<String, LoadSubrequestMessage?>? = null

    @Autowired
    private val kafkaRequestTemplate: KafkaTemplate<String, LoadRequestMessage?>? = null

    @Autowired
    private val kafkaCancelTemplate: KafkaTemplate<String, String>? = null

    fun sendSubrequest(key: String, partition: Int, message: LoadSubrequestMessage?) {
        logger.info(String.format("Producing message -> %s", message))
        kafkaSubrequestTemplate!!.send(Constants.LOAD_SUBREQUEST_TOPIC, partition, key, message)
    }

    fun sendRequest(key: String, message: LoadRequestMessage?) {
        logger.info(String.format("Producing message -> %s", message))
        kafkaRequestTemplate!!.send(Constants.LOAD_REQUEST_TOPIC, key, message)
    }

    fun sendCancel() {
        logger.info(String.format("Producing cancel message "))
        kafkaCancelTemplate!!.send(Constants.LOAD_CANCEL_TOPIC, "")
    }
}