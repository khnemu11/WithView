package com.ssafy.withview.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.repository.dto.LoginDto;
import com.ssafy.withview.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@PostMapping("")
	public LoginDto join(@RequestBody LoginDto loginDto) {
		logger.info("UserController: 회원가입 진행");
		logger.info("loginDto: " + loginDto.getId() + ", " + loginDto.getPassword());
		return userService.join(loginDto);
	}

	@GetMapping("/test")
	public String test() {
		return "유저";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/test2")
	public String test2() {
		return "관리자";
	}
}
