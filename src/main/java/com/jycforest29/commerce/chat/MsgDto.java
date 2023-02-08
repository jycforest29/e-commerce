package com.jycforest29.commerce.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MsgDto {
    private String from;
    private String to;
    private String msg;
    private LocalDateTime sendedAt;

}
