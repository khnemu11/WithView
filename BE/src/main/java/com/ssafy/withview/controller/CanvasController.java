package com.ssafy.withview.controller;

import com.ssafy.withview.repository.CanvasRepository;
import com.ssafy.withview.repository.dto.CanvasDto;
import com.ssafy.withview.repository.dto.JwtDto;
import com.ssafy.withview.repository.dto.LoginDto;
import com.ssafy.withview.repository.dto.UserDto;
import com.ssafy.withview.repository.entity.CanvasEntity;
import com.ssafy.withview.service.CanvasService;
import com.ssafy.withview.service.JwtService;
import com.ssafy.withview.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.simple.JSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/api/canvas")
@RequiredArgsConstructor
@Slf4j
public class CanvasController {
	private final CanvasService canvasService;

	@GetMapping("/{channelSeq}")
	public ResponseEntity<?> findCanvas(@PathVariable(name = "channelSeq")long channelSeq) {
		JSONObject jsonObject = new JSONObject();
		CanvasDto canvasDto;
		try{
			canvasDto = canvasService.findCanvasByChannelSeq(channelSeq);
		}catch (Exception e){
			jsonObject.put("success",false);
			jsonObject.put("msg",e.getMessage());
			return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		jsonObject.put("success",true);
		jsonObject.put("canvas",canvasDto);

		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<?> insertCanvas(@RequestBody CanvasDto canvasDto) {
		JSONObject jsonObject = new JSONObject();
		System.out.println(canvasDto);
		try{
			canvasService.insertCanvas(canvasDto);
		}catch (Exception e){
			jsonObject.put("success",false);
			jsonObject.put("msg",e.getMessage());
			return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		jsonObject.put("success",true);
		jsonObject.put("msg","캔버스 저장을 성공했습니다.");

		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}

	@PostMapping("/{channelSeq}")
	public ResponseEntity<?> updateCanvas(@PathVariable(name="channelSeq")long channelSeq, @RequestBody CanvasDto canvasDto) {
		JSONObject jsonObject = new JSONObject();
		try{
			canvasService.updateCanvas(canvasDto);
		}catch (Exception e){
			jsonObject.put("success",false);
			jsonObject.put("msg",e.getMessage());
			return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		jsonObject.put("success",true);
		jsonObject.put("msg","캔버스 변경을 성공했습니다.");
		
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}
}
