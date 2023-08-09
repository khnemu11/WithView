package com.ssafy.withview.controller;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.dto.CanvasDto;
import com.ssafy.withview.service.CanvasService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CanvasSocketController {
	private final CanvasService canvasService;

	@GetMapping("/{channelSeq}")
	public ResponseEntity<?> findCanvas(@PathVariable(name = "channelSeq")Long channelSeq) {
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
}
