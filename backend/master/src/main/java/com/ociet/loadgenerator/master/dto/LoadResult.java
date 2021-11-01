package com.ociet.loadgenerator.master.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class LoadResult {
    private final String key;
    private final long concurrentUsers;
    private final long averageResponseTime;
    private final long maxResponseTime;
    private final String state;
}
