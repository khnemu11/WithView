package com.ssafy.withview.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChannelValueDto implements Serializable {

	private static final long serialVersionUID = 649467897701384729L;

	private Long serverSeq;
	private Map<Long, Set<Long>> channelValue;
}
