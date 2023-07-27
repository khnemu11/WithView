package com.ssafy.withview.controller;

import com.ssafy.withview.repository.dto.ServerDto;
import com.ssafy.withview.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/test/view")
public class ServerTestController {
	@GetMapping("/server")
	public String findByServerSeq() {
		System.out.println("서버 페이지 실행");
		return "server";
	}
}
