package com.ociet.loadgenerator.common;

import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoadRequestMessage {
    int totalParts;
    int loopCount;
}
