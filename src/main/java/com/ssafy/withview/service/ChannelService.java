package com.ssafy.withview.service;

import com.ssafy.withview.repository.dto.ChannelDto;
import com.ssafy.withview.repository.dto.ServerDto;
import org.json.simple.JSONObject;

import java.util.List;

public interface ChannelService {
	public List<ChannelDto> findAllChannelsByServerSeq(int serverSeq) ;
	public ChannelDto insertChannel(ChannelDto channelDto);
	public ChannelDto updateChannel(ChannelDto channelDto);
	public void deleteChannel(long channelSeq);
}
