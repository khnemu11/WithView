package com.ssafy.withview.service;

import com.ssafy.withview.repository.ChannelRepository;
import com.ssafy.withview.repository.dto.ChannelDto;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService{
	private final ChannelRepository channelRepository;

	public List<ChannelDto> findAllChannelsByServerSeq(int serverSeq){
		List<ChannelDto> list = new ArrayList<>();

		return list;
	}

	@Override
	public ChannelDto insertChannel(ChannelDto channelDto) {
		return null;
	}

	@Override
	public ChannelDto updateChannel(ChannelDto channelDto) {
		return null;
	}

	@Override
	public void deleteChannel(long channelSeq) {
		return ;
	}
}
