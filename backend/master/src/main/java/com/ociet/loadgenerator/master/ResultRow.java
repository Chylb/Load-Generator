package com.ociet.loadgenerator.master;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResultRow {
    public long responseTimeSum;
    public long maxResponseTime;
    public int receivedParts;
    public int totalParts;
    public int loopCount;
    public List<String> errors;
}
