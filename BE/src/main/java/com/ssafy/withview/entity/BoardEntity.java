package com.ssafy.withview.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "board")
@EntityListeners(AuditingEntityListener.class)
public class BoardEntity {
	// 게시글 정보
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;
	private String title;
	private String content;
	@CreationTimestamp
	private LocalDateTime registerTime;
	// 작성자 정보
	private Long userSeq;
	private String nickname;
	// 프리셋 정보
	private String presetId;
	private String presetImgSearchName;

	@Builder
	public BoardEntity(String title, String content, Long userSeq, String nickname, String presetId,
		String presetImgSearchName) {
		this.title = title;
		this.content = content;
		this.userSeq = userSeq;
		this.nickname = nickname;
		this.presetId = presetId;
		this.presetImgSearchName = presetImgSearchName;
	}

	public void updateBoardArticle(String title, String content, String presetId, String presetImgSearchName) {
		this.title = title;
		this.content = content;
		this.presetId = presetId;
		this.presetImgSearchName = presetImgSearchName;
	}
}
