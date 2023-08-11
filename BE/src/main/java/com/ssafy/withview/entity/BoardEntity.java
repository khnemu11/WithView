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
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;
	private Long userSeq;
	private String title;
	private String content;
	private String presetImgSearchName;

	@CreationTimestamp
	private LocalDateTime registerTime;

	@Builder
	public BoardEntity(Long userSeq, String title, String content, String presetImgSearchName) {
		this.userSeq = userSeq;
		this.title = title;
		this.content = content;
		this.presetImgSearchName = presetImgSearchName;
	}
}
