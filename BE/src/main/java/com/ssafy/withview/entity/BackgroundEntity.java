package com.ssafy.withview.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.ssafy.withview.dto.BackgroundDto;
import com.ssafy.withview.dto.StickerDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "background")
public class BackgroundEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;
	private String writer;
	private String originalName;
	private String searchName;
	@ColumnDefault("0")
	private Integer count;
	@CreationTimestamp
	private LocalDateTime createTime;
	private Long userSeq;

	@Builder
	public BackgroundEntity(String originalName, String searchName, Integer count, String writer,Long userSeq) {
		this.originalName = originalName;
		this.searchName = searchName;
		this.count = count;
		this.writer = writer;
		this.userSeq = userSeq;
	}

	public static BackgroundDto toDto(BackgroundEntity stickerEntity){
		if(stickerEntity == null){
			return null;
		}
		return BackgroundDto.builder()
				.seq(stickerEntity.getSeq())
			.writer(stickerEntity.getWriter())
			.originalName(stickerEntity.getOriginalName())
			.searchName(stickerEntity.getSearchName())
			.count(stickerEntity.getCount())
			.createTime(stickerEntity.getCreateTime())
			.userSeq(stickerEntity.getUserSeq())
			.build();
	}

	public void update(StickerDto stickerDto){
		this.originalName = stickerDto.getOriginalName();
		this.searchName = stickerDto.getSearchName();
		this.count = stickerDto.getCount();
	}
}