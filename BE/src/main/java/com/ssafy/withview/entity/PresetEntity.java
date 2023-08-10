package com.ssafy.withview.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ssafy.withview.dto.PresetDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document(collection = "presets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PresetEntity {

	@Id
	private String id;

	private Long userSeq;

	private String presetName;

	private String presetImgSearchName;

	private String stage;

	private LocalDateTime registerTime;

	@Builder
	public PresetEntity(Long userSeq, String presetName, String presetImgSearchName, String stage,
		LocalDateTime registerTime) {
		this.userSeq = userSeq;
		this.presetName = presetName;
		this.presetImgSearchName = presetImgSearchName;
		this.stage = stage;
		this.registerTime = registerTime;
	}

	public static PresetDto toDto(PresetEntity presetEntity) {
		return PresetDto.builder()
			.id(presetEntity.getId())
			.userSeq(presetEntity.getUserSeq())
			.presetName(presetEntity.getPresetName())
			.presetImgSearchName(presetEntity.getPresetImgSearchName())
			.stage(presetEntity.getStage())
			.registerTime(presetEntity.getRegisterTime())
			.build();
	}

}
