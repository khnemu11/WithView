package com.ssafy.withview.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ssafy.withview.entity.FriendsChatEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class FriendsChatDto implements Serializable {

    private static final long serialVersionUID = 64946611254512L;

    private Long friendsChatSeq;
    private Long userSeq;
    private String message;
    private LocalDateTime sendTime;

    public static FriendsChatEntity toEntity(FriendsChatDto dto) {
        return FriendsChatEntity.builder()
                .friendsChatSeq(dto.getFriendsChatSeq())
                .userSeq(dto.getUserSeq())
                .message(dto.getMessage())
                .sendTime(dto.getSendTime())
                .build();
    }

    public String toJson() {
        String json = null;
        try {
            json = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}
