package com.ociet.loadgenerator.master;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResultRow {
    public String url;
    public long responseTimeSum;
    public long maxResponseTime;
    public int receivedParts;
    public int totalParts;
    public int loopCount;
}
