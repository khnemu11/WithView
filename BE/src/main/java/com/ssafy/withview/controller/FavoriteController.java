package com.ssafy.withview.controller;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.dto.ServerDto;
import com.ssafy.withview.service.FavoriteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users/{userSeq}/favorites")
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {
	private final FavoriteService favoriteService;

	@Value(value = "${CLOUD_FRONT_URL}")
	private String CLOUD_FRONT_URL;

	@GetMapping("")
	public ResponseEntity<?> findAllFavorite(@PathVariable long userSeq) {
		log.info("===== 즐겨찾기 목록 =====");
		JSONObject result = new JSONObject();
		try{
			List<ServerDto> serverDtoList= favoriteService.findAllFavoriteByUserSeq(userSeq);

			result.put("success",true);
			result.put("favorite",serverDtoList);
			result.put("imgUriPrefix",CLOUD_FRONT_URL+"server-background/");
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("success",false);
			result.put("msg","즐겨찾기 찾기를 실패했습니다.");
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping("")
	public ResponseEntity<?> insertFavorite(@PathVariable Long userSeq,@RequestParam(name="serverSeq") Long serverSeq) {
		log.info("===== 즐겨찾기 등록 시작 =====");
		JSONObject result = new JSONObject();
		try{
			favoriteService.insertFavoriteByUserSeq(userSeq,serverSeq);

			result.put("success",true);
			result.put("msg","즐겨찾기 등록을 성공했습니다.");
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("success",false);
			result.put("msg","즐겨찾기 등록을 실패했습니다.");
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@DeleteMapping("")
	public ResponseEntity<?> deleteFavorite(@PathVariable Long userSeq,@RequestParam(name="serverSeq") Long serverSeq) {
		log.info("===== 즐겨찾기 등록 시작 =====");
		JSONObject result = new JSONObject();
		try{
			favoriteService.deleteFavoriteByUserSeq(userSeq,serverSeq);

			result.put("success",true);
			result.put("msg","즐겨찾기 삭제를 성공했습니다.");
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("success",false);
			result.put("msg","즐겨찾기 삭제를 실패했습니다.");
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
