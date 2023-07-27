package com.ssafy.withview.controller;

import org.bson.json.JsonObject;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.repository.ServerRepository;
import com.ssafy.withview.repository.dto.ServerDto;
import com.ssafy.withview.repository.entity.ServerEntity;
import com.ssafy.withview.service.ServerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/server")
@RequiredArgsConstructor
public class ServerController {
	private final ServerService serverService;
	@GetMapping("/test")
	public void findByServerSeq() {
		ServerDto inputServerDto= ServerDto.builder()
			.name("롤")
			.hostSeq(1)
			.build();
		System.out.println(inputServerDto);
		inputServerDto = serverService.insertServer(inputServerDto);
		System.out.println(inputServerDto);
		ServerDto serverDto = serverService.findServerBySeq(inputServerDto.getSeq());
		System.out.println(serverDto);
	}
	@PostMapping("/")
	public ResponseEntity<?> findByServerSeq(ServerDto serverDto) {
		JSONObject jsonObject = new JSONObject();
		try{
			serverDto = serverService.insertServer(serverDto);
		}catch (Exception e){
			jsonObject.put("success",false);
			jsonObject.put("msg","서버 추가를 실패했습니다.");
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
		}
		jsonObject.put("success",false);
		jsonObject.put("server",serverDto);
		jsonObject.put("msg","서버 추가를 성공했습니다.");

		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	}
}
