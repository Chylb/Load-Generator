package com.ociet.loadgenerator.master;

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

    @PostMapping
    public void requestLoad(@RequestBody LoadRequest loadRequest) {
        requestService.requestLoad(loadRequest);
    }

    @GetMapping
    public Collection<LoadResult> getResults() {
        return resultService.getResults();
    }
}
