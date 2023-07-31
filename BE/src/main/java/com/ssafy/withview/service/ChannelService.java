package com.ssafy.withview.service;

import com.ssafy.withview.repository.dto.ChannelDto;
import com.ssafy.withview.repository.dto.ServerDto;
import org.json.simple.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChannelService {
	public List<ChannelDto> findAllChannelsByServerSeq(int serverSeq) ;
	public ChannelDto insertChannel(ChannelDto channelDto, MultipartFile multipartFile) throws Exception;
	public ChannelDto updateChannel(ChannelDto channelDto, MultipartFile multipartFile) throws Exception;
	public void deleteChannel(long channelSeq,long userSeq) throws Exception;
}
