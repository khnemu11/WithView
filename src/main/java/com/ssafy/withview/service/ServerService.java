package com.ssafy.withview.service;

import java.util.List;

import com.ssafy.withview.repository.dto.ChannelDto;
import com.ssafy.withview.repository.dto.ServerDto;
import org.json.simple.JSONObject;
import org.springframework.web.multipart.MultipartFile;

public interface ServerService {
	public List<ChannelDto> findAllChannelsByServerSeq(int serverSeq) ;
	public ChannelDto findChannelByName(String channelName);
	public ServerDto insertServer(ServerDto serverDto, MultipartFile multipartFile) throws Exception;
	public ServerDto updateServer(ServerDto serverDto, MultipartFile multipartFile) throws  Exception;
	public ServerDto findServerBySeq(long serverSeq);
	public List<ServerDto> findAllServerByUserSeq(long userSeq);
	public void deleteServer(long serverSeq, long userSeq) throws Exception;
	public List<ServerDto> findAllServer();
}
