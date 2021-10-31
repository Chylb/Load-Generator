package com.ociet.loadgenerator.common;

import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoadRequestMessage {
    private String url;
    private int loopCount;
}
