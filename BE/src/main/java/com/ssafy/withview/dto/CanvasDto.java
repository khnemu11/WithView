package com.ssafy.withview.dto;

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
	private String canvas;

	public static CanvasEntity toEntity(CanvasDto canvasDto){
		if(canvasDto == null){
			return null;
		}
		return CanvasEntity.builder()
			.canvas(canvasDto.getCanvas())
			.channelSeq(canvasDto.getChannelSeq())
			.build();
	}
}