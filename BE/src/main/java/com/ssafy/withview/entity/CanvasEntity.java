package com.ssafy.withview.entity;

import com.ssafy.withview.dto.CanvasDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;

@Getter
@ToString
@Document("canvas" )
@NoArgsConstructor
public class CanvasEntity {
	@Id
	private String id;
	private Long channelSeq;
	private String background;
	private String image;
	private String video;

	public static CanvasDto toDto(CanvasEntity entity){
		return CanvasDto.builder()
				.id(entity.getId())
				.background(entity.getBackground())
				.channelSeq(entity.getChannelSeq())
				.image(entity.getImage())
				.video(entity.getVideo())
				.build();
	}
	public void update(CanvasDto canvasDto){
		this.background = canvasDto.getBackground();
		this.image = canvasDto.getImage();
		this.video = canvasDto.getVideo();
	}
	@Builder
	public CanvasEntity(Long channelSeq,String background, String image,String video) {
		this.channelSeq = channelSeq;
		this.image = image;
		this.background = background;
		this.video = video;
	}
}