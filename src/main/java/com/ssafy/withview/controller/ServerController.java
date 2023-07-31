package com.ssafy.withview.controller;

import com.amazonaws.Response;
import com.amazonaws.services.s3.AmazonS3;
import com.ssafy.withview.repository.dto.ChannelDto;
import com.ssafy.withview.repository.dto.ServerDto;
import com.ssafy.withview.service.ChannelService;
import com.ssafy.withview.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerController {
	private final ServerService serverService;
	private final ChannelService channelService;

	@Value(value = "${CLOUD_FRONT_URL}")
	private String CLOUD_FRONT_URL;

	@PostMapping("/{serverSeq}/channels")
	public ResponseEntity insertChannel(@PathVariable long serverSeq, @ModelAttribute ChannelDto channelDto){
		JSONObject result = new JSONObject();
		try {
			channelService.insertChannel(channelDto);

			result.put("success",true);
		}catch (Exception e){
			e.printStackTrace();

			result.put("success",false);
			result.put("msg","채널 생성 중 오류가 발생했습니다.");
			return new ResponseEntity<JSONObject>(result,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<JSONObject>(result,HttpStatus.OK);
	}
//	@GetMapping("/{serverSeq}/channels/find-channel-by-user")
//	public ResponseEntity findAllChannelsByUser(@PathVariable long serverSeq, @ModelAttribute(name = "userSeq") ChannelDto channelDto){
//		JSONObject result = new JSONObject();
//		try {
//			channelService.insertChannel(channelDto);
//		}catch (Exception e){
//			e.printStackTrace();
//
//			result.put("success",false);
//			result.put("msg","채널 생성 중 오류가 발생했습니다.");
//			return new ResponseEntity<JSONObject>(result,HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		return new ResponseEntity<JSONObject>(result,HttpStatus.OK);
//	}
	@GetMapping("/{serverSeq}")
	public ResponseEntity<?> findServerBySeq(@PathVariable long serverSeq) {
		JSONObject result = new JSONObject();
		try{
			ServerDto serverDto = serverService.findServerBySeq(serverSeq);

			result.put("success",true);
			result.put("server",serverDto);
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("succuess",false);
			result.put("msg",serverSeq+"서버 찾기를 실패했습니다.");
			return new ResponseEntity<JSONObject>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}
	@GetMapping("")
	public ResponseEntity<?> findAllserver() {
		JSONObject result = new JSONObject();
		try{
			List<ServerDto> serverDtoList = serverService.findAllServer();

			result.put("success",true);
			result.put("server",serverDtoList);
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("succuess",false);
			result.put("msg","서버 찾기를 실패했습니다.");
			return new ResponseEntity<JSONObject>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
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
			result.put("succuess",false);
			return new ResponseEntity<JSONObject>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}
	@PostMapping("")
	public ResponseEntity<?> addServer(@ModelAttribute ServerDto serverDto, @RequestParam(name = "file",required = false) MultipartFile multipartFile) {
		System.out.println("====== 서버 추가 시작 ======");
		JSONObject result = new JSONObject();
		try{
			// #1 - 버킷 생성
			serverDto = serverService.insertServer(serverDto,multipartFile);
		}catch (Exception e){
			e.printStackTrace();
			result.put("success",false);
			result.put("msg","서버 추가를 실패했습니다.");
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}

		result.put("success",true);
		result.put("server",serverDto);
		result.put("imgUrl",CLOUD_FRONT_URL+"server-background/"+serverDto.getBackgroundImgSearchName());
		result.put("msg","서버 추가를 성공했습니다.");
		System.out.println("====== 서버 추가 끝 ======");
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}

	@PutMapping("")
	public ResponseEntity<?> updateServer(@ModelAttribute ServerDto serverDto, @RequestParam(name = "file", required = false) MultipartFile multipartFile) {
		System.out.println("====== 서버 변경 시작 ======");
		JSONObject result = new JSONObject();
		try{
			serverDto = serverService.updateServer(serverDto,multipartFile);
		}catch (Exception e){
			e.printStackTrace();
			result.put("success",false);
			result.put("msg","서버 변경 중 오류가 발생했습니다.");
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}

		result.put("success",true);
		result.put("server",serverDto);
		result.put("msg","서버 추가를 성공했습니다.");
		result.put("imgUrl",CLOUD_FRONT_URL+"server-background/"+serverDto.getBackgroundImgSearchName());

		System.out.println("====== 서버 변경 끝 ======");
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}
	@DeleteMapping("")
	public ResponseEntity<?> deleteServer(@RequestBody Map<String,String> request) {
		System.out.println("====== 서버 삭제 시작 ======");
		JSONObject result = new JSONObject();	//결과 json 변수
		try{


		}catch (Exception e){
			e.printStackTrace();
			result.put("success",false);
			result.put("msg","서버 삭제를 실패했습니다.");
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		
		System.out.println("====== 서버 삭제 끝 ======");
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}
}
