package com.ssafy.withview.service;

import java.util.List;

import com.ssafy.withview.dto.ChannelDto;
import com.ssafy.withview.dto.ServerDto;
import com.ssafy.withview.dto.UserDto;

import org.springframework.web.multipart.MultipartFile;

public interface ServerService {
	public ChannelDto findChannelByName(String channelName);
	public ServerDto insertServer(ServerDto serverDto, MultipartFile multipartFile) throws Exception;
	public ServerDto updateServer(ServerDto serverDto, MultipartFile multipartFile) throws  Exception;
	public ServerDto findServerBySeq(long serverSeq);
	public List<ServerDto> findAllServerByUserSeq(long userSeq);
	public List<UserDto> findAllUsersByServerSeq(long serverSeq) ;
	public void deleteServer(long serverSeq, long userSeq) throws Exception;
	public List<ServerDto> findAllServer();
	public void enterServer(long serverSeq, long userSeq) throws Exception;
	public void leaveServer(long serverSeq, long userSeq) throws Exception;
	public String insertInviteCode(long serverSeq, long userSeq) throws Exception;
}
