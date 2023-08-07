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
	public ServerDto findServerBySeq(Long serverSeq);
	public List<ServerDto> findAllServerByUserSeq(Long userSeq);
	public List<UserDto> findAllUsersByServerSeq(Long serverSeq) ;
	public void deleteServer(Long serverSeq, Long userSeq) throws Exception;
	public List<ServerDto> findAllServer();
	public void enterServer(Long serverSeq, Long userSeq) throws Exception;
	public void leaveServer(Long serverSeq, Long userSeq) throws Exception;
	public String insertInviteCode(Long serverSeq, Long userSeq) throws Exception;
	public ServerDto validateInviteCode(String inviteCode) throws Exception;
}
