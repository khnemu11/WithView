package com.ssafy.withview.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ssafy.withview.entity.CanvasEntity;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CanvasDto {
	private String id;
	private Long channelSeq;
	private String background;
	private String image;
	private String video;

	public static CanvasEntity toEntity(CanvasDto canvasDto){
		if(canvasDto == null){
			return null;
		}
		return CanvasEntity.builder()
			.background(canvasDto.getBackground())
			.image(canvasDto.getImage())
			.channelSeq(canvasDto.getChannelSeq())
			.video(canvasDto.getVideo())
			.build();
	}
	public String toJson() {
		String json = null;
		try {
			json = new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return json;
	}
}