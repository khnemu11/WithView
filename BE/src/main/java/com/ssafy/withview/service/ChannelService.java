package com.ssafy.withview.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ssafy.withview.dto.ChannelDto;

public interface ChannelService {
	public List<ChannelDto> findAllChannelsByServerSeq(int serverSeq);

	public ChannelDto insertChannel(ChannelDto channelDto, MultipartFile multipartFile) throws Exception;

	public ChannelDto updateChannel(ChannelDto channelDto, MultipartFile multipartFile) throws Exception;

	public void deleteChannel(long channelSeq, long userSeq) throws Exception;
}
