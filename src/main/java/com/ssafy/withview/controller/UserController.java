package com.ssafy.withview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.repository.dto.LoginDto;
import com.ssafy.withview.service.LoginService;

@RestController
public class UserController {

	@Autowired
	private LoginService loginService;

	@PostMapping("/join")
	public boolean join(@RequestBody LoginDto loginDto) {
		System.out.println("join 실행");
		System.out.println(loginDto.getId() + ", " + loginDto.getPassword());
		if (loginService.join(loginDto).getSeq() > 0) {
			System.out.println("회원가입 성공");
			return true;
		} else {
			System.out.println("회원가입 실패");
			return false;
		}

	}
}
