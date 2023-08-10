package com.ssafy.withview.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.ssafy.withview.dto.ServerDto;
import com.ssafy.withview.dto.StickerDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "sticker")
public class StickerEntity {
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

	@Builder
	public StickerEntity(String originalName, String searchName, Integer count, String writer) {
		this.originalName = originalName;
		this.searchName = searchName;
		this.count = count;
		this.writer = writer;
	}

	public static StickerDto toDto(StickerEntity stickerEntity){
		if(stickerEntity == null){
			return null;
		}
		return StickerDto.builder()
				.seq(stickerEntity.getSeq())
			.writer(stickerEntity.getWriter())
			.originalName(stickerEntity.getOriginalName())
			.searchName(stickerEntity.getSearchName())
			.count(stickerEntity.getCount())
			.createTime(stickerEntity.getCreateTime())
			.build();
	}

	public void update(StickerDto stickerDto){
		this.originalName = stickerDto.getOriginalName();
		this.searchName = stickerDto.getSearchName();
		this.count = stickerDto.getCount();
	}
}