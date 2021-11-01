package com.ociet.loadgenerator.common;

import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoadResultMessage {
    private String requestKey;
    private long responseTimeSum;
    private long maxResponseTime;
    private long startTimestamp;
    private boolean failed;
}
