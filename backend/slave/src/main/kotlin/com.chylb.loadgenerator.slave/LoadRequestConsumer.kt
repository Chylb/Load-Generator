package com.chylb.loadgenerator.slave

import com.chylb.loadgenerator.common.Constants
import com.chylb.loadgenerator.common.LoadResultMessage
import com.chylb.loadgenerator.common.LoadSubrequestMessage
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference


@Service
class LoadRequestConsumer {
    companion object {
        private const val MAX_CONSUME_TIME_DELAY: Long = 1000
        private val logger: Logger = LoggerFactory.getLogger(LoadRequestConsumer::class.java)
    }
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val executor: ThreadPoolExecutor =
        Executors.newFixedThreadPool(Constants.CONCURRENT_USERS_PER_SLAVE) as ThreadPoolExecutor

    @Autowired
    private val producer: LoadResultProducer? = null
    private val requestGenerator: ThrowingFunction<HttpRequestGeneratorArguments, HttpRequest> =
        ThrowingFunction { args: HttpRequestGeneratorArguments ->
            HttpRequest.newBuilder(
                URI.create("http://localhost:1111/players/" + args.userOffset % 18278)
            )
                .build()
        }

    @KafkaListener(topics = [Constants.LOAD_SUBREQUEST_TOPIC], groupId = "group_id")
    @Throws(
        IOException::class,
        InterruptedException::class
    )
    fun consume(record: ConsumerRecord<String?, String?>) {
        logger.info(java.lang.String.format("Consumed message -> %s", record.key()))
        val message: LoadSubrequestMessage = objectMapper.readValue(record.value(), LoadSubrequestMessage::class.java)
        val latch = CountDownLatch(Constants.CONCURRENT_USERS_PER_SLAVE)
        val startTimestamp = System.currentTimeMillis()
        val responseTimeSum = AtomicLong()
        val maxResponseTime = AtomicLong()
        val error = AtomicReference<String?>(null)
        if (Math.abs(record.timestamp() - startTimestamp) > MAX_CONSUME_TIME_DELAY) {
            error.set("Exceeded max consume time delay")
        }
        if (error.get() == null) {
            for (i in 0 until Constants.CONCURRENT_USERS_PER_SLAVE) {
                val requestGeneratorArguments = HttpRequestGeneratorArguments()
                requestGeneratorArguments.userOffset = message.requestOffset * Constants.CONCURRENT_USERS_PER_SLAVE + i
                executor.execute {
                    val client: HttpClient = HttpClient.newHttpClient()
                    var userResponseTimeSum: Long = 0
                    var userMaxResponseTime: Long = 0
                    try {
                        for (j in 0 until message.loopCount) {
                            val httpRequest: HttpRequest = requestGenerator.apply(requestGeneratorArguments)
                            val start = System.nanoTime()
                            val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())
                            val responseTime = System.nanoTime() - start
                            userResponseTimeSum += responseTime
                            userMaxResponseTime = Math.max(userMaxResponseTime, responseTime)
                            requestGeneratorArguments.loopIteration = j
                            requestGeneratorArguments.previousResponse = response
                        }
                    } catch (e: Exception) {
                        var exceptionMessage = e.message
                        if (exceptionMessage == null) {
                            exceptionMessage = e.javaClass.name
                        }
                        logger.error(exceptionMessage)
                        error.set(exceptionMessage)
                    }
                    responseTimeSum.addAndGet(userResponseTimeSum)
                    val finalUserMaxResponseTime = userMaxResponseTime
                    maxResponseTime.updateAndGet { x: Long ->
                        Math.max(
                            x,
                            finalUserMaxResponseTime
                        )
                    }
                    latch.countDown()
                }
            }
            latch.await()
        }
        val resultMessage = LoadResultMessage(
            record.key(),
            responseTimeSum.get(),
            maxResponseTime.get(),
            startTimestamp,
            error.get()
        )
        producer!!.send(resultMessage)
    }

    class HttpRequestGeneratorArguments(
        var userOffset: Int = 0,
        var loopIteration: Int = 0,
        var previousResponse: HttpResponse<String>? = null,
    )
}