package com.ssafy.withview.repository.entity;

import com.ssafy.withview.repository.dto.CanvasDto;
import com.ssafy.withview.repository.dto.ChannelDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;

@Getter
@ToString
@Document("canvas")
@NoArgsConstructor
public class CanvasEntity {
	@Id
	private String id;
	private long channelSeq;
	private String canvas;

	public static CanvasDto toDto(CanvasEntity entity){
		return CanvasDto.builder()
				.id(entity.getId())
				.canvas(entity.getCanvas())
				.channelSeq(entity.getChannelSeq())
				.build();
	}
	public void update(CanvasDto canvasDto){
		this.canvas = canvasDto.getCanvas();
	}
	@Builder
	public CanvasEntity(long channelSeq,String canvas) {
		this.channelSeq = channelSeq;
		this.canvas = canvas;
	}
}