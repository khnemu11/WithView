package com.ssafy.withview.dto;

import java.time.LocalDateTime;

import com.ssafy.withview.entity.CanvasEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InviteDto {
	private String url;
	private ServerDto serverDto;
	private LocalDateTime createTime;
}