package com.ssafy.withview.controller;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.withview.dto.BackgroundDto;
import com.ssafy.withview.dto.StickerDto;
import com.ssafy.withview.service.BackgroundService;
import com.ssafy.withview.service.StickerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/backgrounds")
@RequiredArgsConstructor
@Slf4j
public class BackgroundController {
	private final BackgroundService backgroundService;

	@GetMapping("/users")
	public ResponseEntity<?> findAllUserBackgrounds(BackgroundDto backgroundDto) {
		JSONObject jsonObject = new JSONObject();
		log.info("==== 유저 배경 검색 시작 ====");
		try{
			List<BackgroundDto> backgroundDtoList = backgroundService.findAllBackgroundsByUserSeq(backgroundDto);
			jsonObject.put("success",true);
			jsonObject.put("stickers",backgroundDtoList);
		}catch (Exception e){
			jsonObject.put("success",false);
			jsonObject.put("msg",e.getMessage());
			return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}
	// @GetMapping("/basic")
	// public ResponseEntity<?> findAllBasicBackgrounds() {
	// 	JSONObject jsonObject = new JSONObject();
	// 	log.info("==== 기본 배경 검색 시작 ====");
	// 	try{
	// 		List<BackgroundDto> backgroundDtoList = backgroundService.findAllBackgroundsByUserSeq(0L);
	// 		jsonObject.put("success",true);
	// 		jsonObject.put("stickers",backgroundDtoList);
	// 	}catch (Exception e){
	// 		jsonObject.put("success",false);
	// 		jsonObject.put("msg",e.getMessage());
	// 		return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
	// 	}
	// 	return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	// }

	@GetMapping("")
	public ResponseEntity<?> findAllBackgrounds() {
		JSONObject jsonObject = new JSONObject();
		log.info("==== 모든 배경 검색 시작 ====");
		try{
			List<BackgroundDto> backgroundDtoList = backgroundService.findAllBackgrounds();
			jsonObject.put("success",true);
			jsonObject.put("stickers",backgroundDtoList);
		}catch (Exception e){
			jsonObject.put("success",false);
			jsonObject.put("msg",e.getMessage());
			return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}

	@PostMapping("")
	public ResponseEntity<?> insertBackground(BackgroundDto backgroundDto, @RequestParam(name ="file")MultipartFile multipartFile) {
		log.info("=== 스티커 등록 시작 ===");
		JSONObject jsonObject = new JSONObject();
		try{
			backgroundDto = backgroundService.insertBackground(backgroundDto,multipartFile);
		}catch (Exception e){
			e.printStackTrace();
			jsonObject.put("success",false);
			jsonObject.put("msg",e.getMessage());
			return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		jsonObject.put("success",true);
		jsonObject.put("sticker",backgroundDto);

		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}

	@DeleteMapping("/{backgroundSeq}")
	public ResponseEntity<?> deleteBackground(@PathVariable(name="backgroundSeq")Long backgroundSeq) {
		JSONObject jsonObject = new JSONObject();
		try{
			backgroundService.deleteBackground(backgroundSeq);
		}catch (Exception e){
			jsonObject.put("success",false);
			jsonObject.put("msg",e.getMessage());
			return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		jsonObject.put("success",true);
		jsonObject.put("msg","배경 삭제를 성공했습니다.");
		
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}
}
