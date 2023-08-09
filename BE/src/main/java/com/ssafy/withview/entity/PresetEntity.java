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
	private String presetName;
	private String presetImgSearchName;
	private String stage;
	@CreationTimestamp
	private LocalDateTime registerTime;

	@Builder
	public PresetEntity(Long userSeq, String presetName, String presetImgSearchName, String stage) {
		this.userSeq = userSeq;
		this.presetName = presetName;
		this.presetImgSearchName = presetImgSearchName;
		this.stage = stage;
	}
}
