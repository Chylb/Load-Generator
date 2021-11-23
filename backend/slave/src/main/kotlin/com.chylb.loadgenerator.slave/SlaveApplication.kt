package com.chylb.loadgenerator.slave

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient


@SpringBootApplication
@EnableDiscoveryClient
open class SlaveApplication

fun main(args: Array<String>) {
    SpringApplication.run(SlaveApplication::class.java, *args)
}
