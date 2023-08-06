package com.ssafy.withview.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChannelValueDto implements Serializable {

	private static final long serialVersionUID = 649467897738472912L;

	private Long serverSeq;
	public Map<Long, Set<Long>> channelMember;

	public ChannelValueDto(Long serverSeq) {
		this.serverSeq = serverSeq;
	}

	public String toJson() {
		String json = null;
		try {
			json = new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return json;
	}
}
