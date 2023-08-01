package com.ssafy.withview.controller;

import com.ssafy.withview.repository.dto.ChannelDto;
import com.ssafy.withview.repository.dto.ServerDto;
import com.ssafy.withview.repository.dto.UserDto;
import com.ssafy.withview.service.ChannelService;
import com.ssafy.withview.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerController {
	private final ServerService serverService;
	private final ChannelService channelService;

	@Value(value = "${CLOUD_FRONT_URL}")
	private String CLOUD_FRONT_URL;

	@GetMapping("/{serverSeq}")
	public ResponseEntity<?> findServerBySeq(@PathVariable long serverSeq) {
		JSONObject result = new JSONObject();
		try{
			ServerDto serverDto = serverService.findServerBySeq(serverSeq);

			result.put("success",true);
			result.put("server",serverDto);
			result.put("imgUriPrefix",CLOUD_FRONT_URL+"server-background/");
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("success",false);
			result.put("msg",serverSeq+"서버 찾기를 실패했습니다.");
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("")
	public ResponseEntity<?> findAllServers() {
		JSONObject result = new JSONObject();
		try{
			List<ServerDto> serverDtoList = serverService.findAllServer();

			result.put("success",true);
			result.put("servers",serverDtoList);
			result.put("imgUriPrefix",CLOUD_FRONT_URL+"server-background/");
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("success",false);
			result.put("msg","서버 찾기를 실패했습니다.");
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@GetMapping("/{serverSeq}/users")
	public ResponseEntity<?> findAllUsersInServer(@PathVariable(name = "serverSeq") long serverSeq) {
		JSONObject result = new JSONObject();
		try{
			List<UserDto> userDtoList = serverService.findAllUsersByServerSeq(serverSeq);
			result.put("success",true);
			result.put("users",userDtoList);
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("success",false);
			result.put("msg","서버 참가자 목록 불러오기를 실패했습니다.");
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@GetMapping("/find-server-by-user")
	public ResponseEntity<?> findServerByUser(@RequestParam("userSeq") long userSeq) {
		JSONObject result = new JSONObject();
		try{
			List<ServerDto> serverDtoList = serverService.findAllServerByUserSeq(userSeq);
			result.put("success",true);
			result.put("servers",serverDtoList);
			result.put("imgUriPrefix",CLOUD_FRONT_URL+"server-background/");
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("success",false);
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@PostMapping("")
	public ResponseEntity<?> insertServer(@ModelAttribute ServerDto serverDto,@RequestParam(name = "file",required = false) MultipartFile multipartFile) {
		System.out.println("====== 서버 추가 시작 ======");
		JSONObject result = new JSONObject();
		try{
			serverDto = serverService.insertServer(serverDto,multipartFile);
			System.out.println("생성한 서버"+serverDto);
			serverService.enterServer(serverDto.getSeq(),serverDto.getHostSeq());
		}catch (Exception e){
			e.printStackTrace();
			result.put("success",false);
			result.put("msg",e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		result.put("success",true);
		result.put("server",serverDto);
		result.put("imgUrl",CLOUD_FRONT_URL+"server-background/"+serverDto.getBackgroundImgSearchName());
		result.put("msg","서버 추가를 성공했습니다.");
		System.out.println("====== 서버 추가 끝 ======");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@PostMapping("/enter")
	public ResponseEntity<?> enterServer(@RequestParam(name="serverSeq")long serverSeq,@RequestParam(name="userSeq")long userSeq) {
		System.out.println("====== 서버 입장 시작 ======");
		System.out.println("서버 seq" + serverSeq);
		System.out.println("유저 seq" + userSeq);
		JSONObject result = new JSONObject();
		try{
			// #1 - 버킷 생성
			serverService.enterServer(serverSeq,userSeq);
		}catch (Exception e){
			e.printStackTrace();
			result.put("success",false);
			result.put("msg","서버 입장을 실패했습니다.");
			return new ResponseEntity<>(result, HttpStatus.OK);
		}

		result.put("success",true);
		result.put("msg","서버 입장울 성공했습니다.");
		System.out.println("====== 서버 추가 끝 ======");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@DeleteMapping("/leave")
	public ResponseEntity<?> leaveServer(@RequestParam(name="serverSeq")long serverSeq,@RequestParam(name="userSeq")long userSeq) {
		System.out.println("====== 서버 퇴장 시작 ======");
		JSONObject result = new JSONObject();
		try{
			// #1 - 버킷 생성
			serverService.leaveServer(serverSeq,userSeq);
		}catch (Exception e){
			e.printStackTrace();
			result.put("success",false);
			result.put("msg","서버 퇴장을 실패했습니다.");
			return new ResponseEntity<>(result, HttpStatus.OK);
		}

		result.put("success",true);
		result.put("msg","서버 퇴장을 성공했습니다.");
		System.out.println("====== 서버 퇴장 끝 ======");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@PostMapping("/{serverSeq}")
	public ResponseEntity<?> updateServer(@PathVariable(name = "serverSeq") long serverSeq,@ModelAttribute ServerDto serverDto, @RequestParam(name = "file", required = false) MultipartFile multipartFile) {
		System.out.println("====== 서버 변경 시작 ======");
		JSONObject result = new JSONObject();
		try{
			serverDto = serverService.updateServer(serverDto,multipartFile);

			result.put("success",true);
			result.put("server",serverDto);
			result.put("msg","서버 수정을 성공했습니다.");
			result.put("imgUrl",CLOUD_FRONT_URL+"server-background/"+serverDto.getBackgroundImgSearchName());

		}catch (Exception e){
			e.printStackTrace();
			result.put("success",false);
			result.put("msg","서버 변경 중 오류가 발생했습니다.");
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		System.out.println("====== 서버 변경 끝 ======");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@DeleteMapping("")
	public ResponseEntity<?> deleteServer(@RequestParam(name="serverSeq") long serverSeq,
										  @RequestParam(name="userSeq") long userSeq) {
		System.out.println("====== 서버 삭제 시작 ======");
		JSONObject result = new JSONObject();	//결과 json 변수
		try{
			serverService.deleteServer(serverSeq,userSeq);
			result.put("success",true);
			result.put("msg","서버 삭제를 성공했습니다.");
		}catch (Exception e){
			e.printStackTrace();
			result.put("success",false);
			result.put("msg",e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		
		System.out.println("====== 서버 삭제 끝 ======");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@GetMapping("/{serverSeq}/channels")
	public ResponseEntity<?> findAllChannelsByServer(@PathVariable long serverSeq){
		JSONObject result = new JSONObject();
		System.out.println("====== 서버 내 모든 채널 탐색 시작 ======");
		try {
			List<ChannelDto> channelDtos = channelService.findAllChannelsByServerSeq(serverSeq);

			result.put("success",true);
			result.put("channels",channelDtos);
		}catch (Exception e){
			e.printStackTrace();

			result.put("success",false);
			result.put("msg","채널 탐색 중 오류가 발생했습니다.");
			return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result,HttpStatus.OK);
	}
	@PostMapping("/{serverSeq}/channels/{channelSeq}")
	public ResponseEntity<?> updateChannel(@PathVariable(name = "serverSeq") long serverSeq,@PathVariable(name = "channelSeq") long channelSeq, @ModelAttribute ChannelDto channelDto,@RequestParam(name = "file") MultipartFile multipartFile){
		JSONObject result = new JSONObject();
		System.out.println("====== 채널 수정 시작 ======");
		try {
			channelDto.setServerSeq(serverSeq);
			channelDto.setSeq(channelSeq);
			channelDto = channelService.updateChannel(channelDto,multipartFile,serverSeq);

			result.put("success",true);
			result.put("channel",channelDto);
			result.put("imgUrlPrefix",CLOUD_FRONT_URL+"channel-background/");
			result.put("msg","채널 변경을 성공했습니다.");
		}catch (Exception e){
			e.printStackTrace();

			result.put("success",false);
			result.put("msg","채널 수정 중 오류가 발생했습니다!");
			return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result,HttpStatus.OK);
	}
	@PostMapping("/{serverSeq}/channels")
	public ResponseEntity<?> insertChannel(@PathVariable long serverSeq, @ModelAttribute ChannelDto channelDto,@RequestParam(name = "file") MultipartFile multipartFile){
		JSONObject result = new JSONObject();
		System.out.println("====== 채널 생성 시작 ======");
		try {
			channelDto.setServerSeq(serverSeq);
			channelDto = channelService.insertChannel(channelDto,multipartFile,serverSeq);
			
			result.put("success",true);
			result.put("channel",channelDto);
			result.put("imgUrlPrefix",CLOUD_FRONT_URL+"channel-background/");
			result.put("msg","채널 생성을 성공했습니다.");
		}catch (Exception e){
			e.printStackTrace();

			result.put("success",false);
			result.put("msg","채널 생성 중 오류가 발생했습니다!");
			return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result,HttpStatus.OK);
	}
	@DeleteMapping("/{serverSeq}/channels/{channelSeq}")
	public ResponseEntity<?> deleteChannel(@PathVariable(name="serverSeq") long serverSeq, @PathVariable(name="channelSeq") long channelSeq){
		JSONObject result = new JSONObject();
		System.out.println("====== 채널 삭제 시작 ======");
		try {
			channelService.deleteChannel(channelSeq);
			result.put("success",true);
			result.put("msg","채널 삭제를 성공했습니다.");
		}catch (Exception e){
			e.printStackTrace();

			result.put("success",false);
			result.put("msg","채널 수정 중 오류가 발생했습니다!");
			return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result,HttpStatus.OK);
	}
	@GetMapping("/{serverSeq}/channels/{channelSeq}")
	public ResponseEntity<?> findChannel(@PathVariable(name = "serverSeq") long serverSeq,@PathVariable(name = "channelSeq") long channelSeq) {
		System.out.println("====== 채널 탐색 시작 ======");
		JSONObject result = new JSONObject();
		try{
			ChannelDto channelDto = channelService.findChannelByChannelSeq(channelSeq);

			result.put("success",true);
			result.put("channel",channelDto);
			result.put("imgPrefix",CLOUD_FRONT_URL+"channel-background/");
		}catch (Exception e){
			e.printStackTrace();
			result.put("success",false);
			result.put("msg","서버 탐색을 실패했습니다.");
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		
		System.out.println("====== 채널 탐색 끝 ======");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
