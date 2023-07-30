package com.ssafy.withview.service;

import java.util.List;

import com.ssafy.withview.repository.dto.ChannelDto;
import com.ssafy.withview.repository.dto.ServerDto;
import org.json.simple.JSONObject;

public interface ServerService {
	public List<ChannelDto> findAllChannelsByServerSeq(int serverSeq) ;
	public ChannelDto findChannelByName(String channelName);
	public ServerDto insertServer(ServerDto serverDto);
	public ServerDto updateServer(ServerDto serverDto) throws  Exception;
	public ServerDto findServerBySeq(long serverSeq);
	public List<ServerDto> findAllServerByUserSeq(long userSeq);
	public JSONObject deleteServer(long serverSeq, long userSeq);
}
