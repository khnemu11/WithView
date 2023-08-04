package com.ssafy.withview.dto;

import java.io.Serializable;
import java.util.List;
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
	public Map<Long, Set<Long>> temp;
	private List<Long> channelSeq;
	private List<Set<Long>> userSeq;
}
