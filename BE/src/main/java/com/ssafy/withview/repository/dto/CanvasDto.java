package com.ssafy.withview.repository.dto;

import com.ssafy.withview.repository.entity.CanvasEntity;
import com.ssafy.withview.repository.entity.ServerEntity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CanvasDto {
	private String	 id;
	private long channelSeq;
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