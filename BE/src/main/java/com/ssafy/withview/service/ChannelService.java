package com.ssafy.withview.service;

import com.ssafy.withview.dto.ChannelDto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChannelService {
	public List<ChannelDto> findAllChannelsByServerSeq(Long serverSeq) ;
	public ChannelDto findChannelByChannelSeq(Long channelSeq) ;
	public ChannelDto insertChannel(ChannelDto channelDto, MultipartFile multipartFile,Long serverSeq) throws Exception;
	public ChannelDto updateChannel(ChannelDto channelDto, MultipartFile multipartFile,Long serverSeq) throws Exception;
	public void deleteChannel(Long channelSeq) throws Exception;
}
