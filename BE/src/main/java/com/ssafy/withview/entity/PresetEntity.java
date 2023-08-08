package com.ssafy.withview.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
	private String stage;
	private String presetName;
	private String presetImgSearchName;
	@CreationTimestamp
	private LocalDateTime registerTime;

	@Builder
	public PresetEntity(Long userSeq, String stage, String presetName, String presetImgSearchName) {
		this.userSeq = userSeq;
		this.stage = stage;
		this.presetName = presetName;
		this.presetImgSearchName = presetImgSearchName;
	}
}
