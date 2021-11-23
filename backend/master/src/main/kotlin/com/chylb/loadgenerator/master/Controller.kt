package com.chylb.loadgenerator.master

import com.chylb.loadgenerator.master.dto.LoadRequest
import com.netflix.discovery.shared.Application
import com.netflix.eureka.EurekaServerContextHolder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@CrossOrigin(origins = ["*"])
class Controller @Autowired internal constructor(requestService: RequestService) {
    private val requestService: RequestService
    @PostMapping(path = ["/load"])
    fun requestLoad(@RequestBody loadRequest: LoadRequest?) {
        if (loadRequest != null) {
            requestService.requestLoad(loadRequest)
        }
    }

    @GetMapping(path = ["/slaves"])
    fun slaveCount(): Int {
        val application: Application? =
            EurekaServerContextHolder.getInstance().serverContext.registry.getApplication("SLAVE2")
        return if (application == null) 0 else application.size()
    }

    init {
        this.requestService = requestService
    }
}