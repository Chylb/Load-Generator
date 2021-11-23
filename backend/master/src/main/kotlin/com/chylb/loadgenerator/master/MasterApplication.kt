package com.chylb.loadgenerator.master

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
open class MasterApplication

fun main(args: Array<String>) {
    SpringApplication.run(MasterApplication::class.java, *args)
}