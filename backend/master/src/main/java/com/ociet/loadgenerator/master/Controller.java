package com.ociet.loadgenerator.master;

import com.netflix.discovery.shared.Application;
import com.netflix.eureka.EurekaServerContextHolder;
import com.ociet.loadgenerator.master.dto.LoadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class Controller {
    private final RequestService requestService;

    @Autowired
    Controller(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping(path="/load")
    public void requestLoad(@RequestBody LoadRequest loadRequest) {
        requestService.requestLoad(loadRequest);
    }

    @GetMapping(path="/slaves")
    public int getSlaveCount() {
        Application application = EurekaServerContextHolder.getInstance().getServerContext().getRegistry().getApplication("SLAVE2");
        return application == null ? 0 : application.size();
    }
}
