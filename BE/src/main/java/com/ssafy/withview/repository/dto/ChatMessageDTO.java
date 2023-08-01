package com.ssafy.withview.repository.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatMessageDTO {
    public enum MessageType {
        ENTER, TALK, LEAVE
    }

    private MessageType type;
    private String serverSeq;
    private String sender;
    private String message;
    private Long userCount;
}
