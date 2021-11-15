package com.ociet.loadgenerator.master;

import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.eureka.EurekaServerContextHolder;
import com.ociet.loadgenerator.master.dto.LoadRequest;
import com.ociet.loadgenerator.master.dto.LoadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@CrossOrigin(origins = "*")
public class Controller {
    private final RequestService requestService;
    private final ResultService resultService;

    @Autowired
    Controller(RequestService requestService, ResultService resultService) {
        this.requestService = requestService;
        this.resultService = resultService;
    }

    @PostMapping(path="/load")
    public void requestLoad(@RequestBody LoadRequest loadRequest) {
        requestService.requestLoad(loadRequest);
    }

    @GetMapping(path="/load")
    public Collection<LoadResult> getResults() {
        return resultService.getResults();
    }

    @GetMapping(path="/slaves")
    public int getSlaveCount() {
        Application application = EurekaServerContextHolder.getInstance().getServerContext().getRegistry().getApplication("SLAVE2");
        return application == null ? 0 : application.size();
    }
}
