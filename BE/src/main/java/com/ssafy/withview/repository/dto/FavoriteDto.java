package com.ssafy.withview.repository.dto;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ssafy.withview.repository.entity.FavoriteEntity;
import com.ssafy.withview.repository.entity.ServerEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "favorite")
public class FavoriteDto {
	private Long seq;
	private Long serverSeq;
	private Long userSeq;
	private UserDto userDto;
	private ServerDto serverDto;
}