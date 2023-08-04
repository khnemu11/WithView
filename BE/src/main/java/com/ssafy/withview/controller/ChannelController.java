package com.ssafy.withview.controller;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.withview.dto.ChannelDto;
import com.ssafy.withview.service.ChannelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/servers/{serverSeq}/channels")
@RequiredArgsConstructor
@Slf4j
public class ChannelController {
	private final ChannelService channelService;

	@Value(value = "${CLOUD_FRONT_URL}")
	private String CLOUD_FRONT_URL;

	@GetMapping("")
	public ResponseEntity<?> findAllChannelsByServer(@PathVariable Long serverSeq){
		JSONObject result = new JSONObject();
		log.info("====== 서버 내 모든 채널 탐색 시작 ======");
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
	@PostMapping("/{channelSeq}")
	public ResponseEntity<?> updateChannel(@PathVariable(name = "serverSeq") Long serverSeq,@PathVariable(name = "channelSeq") Long channelSeq, @ModelAttribute ChannelDto channelDto,@RequestParam(name = "file") MultipartFile multipartFile){
		JSONObject result = new JSONObject();
		log.info("====== 채널 수정 시작 ======");
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
	@PostMapping("")
	public ResponseEntity<?> insertChannel(@PathVariable Long serverSeq, @ModelAttribute ChannelDto channelDto,@RequestParam(name = "file") MultipartFile multipartFile){
		JSONObject result = new JSONObject();
		log.info("====== 채널 생성 시작 ======");
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
	@DeleteMapping("/{channelSeq}")
	public ResponseEntity<?> deleteChannel(@PathVariable(name="serverSeq") Long serverSeq, @PathVariable(name="channelSeq") Long channelSeq){
		JSONObject result = new JSONObject();
		log.info("====== 채널 삭제 시작 ======");
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
	@GetMapping("/{channelSeq}")
	public ResponseEntity<?> findChannel(@PathVariable(name = "serverSeq") Long serverSeq,@PathVariable(name = "channelSeq") Long channelSeq) {
		log.info("====== 채널 탐색 시작 ======");
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

		log.info("====== 채널 탐색 끝 ======");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
