package com.ssafy.withview.repository.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
    public enum MessageType {
        ENTER, TALK, LEAVE
    }

    private MessageType type;
    private String serverSeq;
    private String sender;
    private String message;
}
