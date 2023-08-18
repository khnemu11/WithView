package com.ssafy.withview.dto;

import javax.persistence.Table;

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