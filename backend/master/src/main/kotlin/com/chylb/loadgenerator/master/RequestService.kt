package com.chylb.loadgenerator.master

import com.chylb.loadgenerator.common.Constants
import com.chylb.loadgenerator.common.LoadRequestMessage
import com.chylb.loadgenerator.common.LoadSubrequestMessage
import com.chylb.loadgenerator.master.dto.LoadRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RequestService @Autowired internal constructor(producer: Producer) {
    companion object {
        private const val PARTITION_COUNT = 100
    }

    private val producer: Producer
    fun requestLoad(loadRequest: LoadRequest) {
        val loadSubrequestMessage = LoadSubrequestMessage()
        loadSubrequestMessage.loopCount = loadRequest.loopCount
        val concurrentInstances: Int = loadRequest.concurrentUsers / Constants.CONCURRENT_USERS_PER_SLAVE
        val key = System.currentTimeMillis().toString()
        val loadRequestMessage = LoadRequestMessage(concurrentInstances, loadRequest.loopCount)
        producer.sendRequest(key, loadRequestMessage)
        for (i in 0 until concurrentInstances) {
            loadSubrequestMessage.requestOffset = i
            var partition = Math.round(i / concurrentInstances.toFloat() * PARTITION_COUNT) + 1
            partition = Math.min(partition, PARTITION_COUNT - 1)
            producer.sendSubrequest(key, partition, loadSubrequestMessage)
        }
    }

    init {
        this.producer = producer
    }
}