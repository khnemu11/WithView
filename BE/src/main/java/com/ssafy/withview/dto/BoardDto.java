package com.ssafy.withview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
	private Long seq;
	private Long userSeq;
	private String title;
	private String content;
	private String presetImgSearchName;
}
