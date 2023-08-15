package com.ssafy.withview.controller;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.withview.dto.CanvasDto;
import com.ssafy.withview.dto.StickerDto;
import com.ssafy.withview.service.CanvasService;
import com.ssafy.withview.service.StickerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/stickers")
@RequiredArgsConstructor
@Slf4j
public class StickerController {
	private final StickerService stickerService;

	@GetMapping("/users")
	public ResponseEntity<?> findAllUserSticker(@ModelAttribute StickerDto stickerDto) {
		JSONObject jsonObject = new JSONObject();


		log.info("==== 유저 스티커 검색 시작 ====");
		try{
			List<StickerDto> stickerDtoList = stickerService.findAllStickersByUserSeq(stickerDto);
			jsonObject.put("success",true);
			jsonObject.put("stickers",stickerDtoList);
		}catch (Exception e){
			jsonObject.put("success",false);
			jsonObject.put("msg",e.getMessage());
			return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}
	@GetMapping("/basic")
	public ResponseEntity<?> findAllBasicSticker(StickerDto stickerDto) {
		JSONObject jsonObject = new JSONObject();
		log.info("==== 기본 스티커 검색 시작 ====");
		try{
			List<StickerDto> stickerDtoList = stickerService.findAllStickersByUserSeq(stickerDto);
			jsonObject.put("success",true);
			jsonObject.put("stickers",stickerDtoList);
		}catch (Exception e){
			jsonObject.put("success",false);
			jsonObject.put("msg",e.getMessage());
			return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}

	@GetMapping("")
	public ResponseEntity<?> findAllSticker() {
		JSONObject jsonObject = new JSONObject();
		log.info("==== 모든 스티커 검색 시작 ====");
		try{
			List<StickerDto> stickerDtoList = stickerService.findAllStickers();
			jsonObject.put("success",true);
			jsonObject.put("stickers",stickerDtoList);
		}catch (Exception e){
			jsonObject.put("success",false);
			jsonObject.put("msg",e.getMessage());
			return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}
	@GetMapping("/find-by-name")
	public ResponseEntity<?> findStickerByName(@RequestParam(name="keyword") String keyword) {
		JSONObject jsonObject = new JSONObject();
		log.info("==== 스티커 이름 검색 시작 ====");
		try{
			List<StickerDto> stickerDtoList = stickerService.findAllStickersByName(keyword);
			jsonObject.put("success",true);
			jsonObject.put("stickers",stickerDtoList);
		}catch (Exception e){
			jsonObject.put("success",false);
			jsonObject.put("msg",e.getMessage());
			return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}
	@PostMapping("")
	public ResponseEntity<?> insertSticker(StickerDto stickerDto, @RequestParam(name ="file")MultipartFile multipartFile) {
		log.info("=== 스티커 등록 시작 ===");
		JSONObject jsonObject = new JSONObject();
		try{
			stickerDto = stickerService.insertSticker(stickerDto,multipartFile);
		}catch (Exception e){
			e.printStackTrace();
			jsonObject.put("success",false);
			jsonObject.put("msg",e.getMessage());
			return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		jsonObject.put("success",true);
		jsonObject.put("sticker",stickerDto);

		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}

	@DeleteMapping("/{stickerSeq}")
	public ResponseEntity<?> deleteSticker(@PathVariable(name="stickerSeq")Long stickerSeq) {
		JSONObject jsonObject = new JSONObject();
		try{
			stickerService.deleteSticker(stickerSeq);
		}catch (Exception e){
			jsonObject.put("success",false);
			jsonObject.put("msg",e.getMessage());
			return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		jsonObject.put("success",true);
		jsonObject.put("msg","스티커 삭제를 성공했습니다.");
		
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}
}
