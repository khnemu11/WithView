package com.ssafy.withview.service;

import com.ssafy.withview.dto.ChannelDto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChannelService {
	public List<ChannelDto> findAllChannelsByServerSeq(long serverSeq) ;
	public ChannelDto findChannelByChannelSeq(long channelSeq) ;
	public ChannelDto insertChannel(ChannelDto channelDto, MultipartFile multipartFile,long serverSeq) throws Exception;
	public ChannelDto updateChannel(ChannelDto channelDto, MultipartFile multipartFile,long serverSeq) throws Exception;
	public void deleteChannel(long channelSeq) throws Exception;
}
