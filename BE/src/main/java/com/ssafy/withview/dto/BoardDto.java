package com.ssafy.withview.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	// 게시글 정보
	private Long boardSeq;
	private String title;
	private String content;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime registerTime;
	// 작성자 정보
	private Long userSeq;
	private String nickname;
	private String profileImgSearchName;
	// 프리셋 정보
	private String presetId;
	private String presetImgSearchName;
	private String stage;
}
