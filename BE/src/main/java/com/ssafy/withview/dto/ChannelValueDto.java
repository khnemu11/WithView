package com.ssafy.withview.dto;

import java.util.Map;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChannelValueDto {

	private Long serverSeq;
	private Map<ChatRoomDto, Set<Long>> channelValue;
}
