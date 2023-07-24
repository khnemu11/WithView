package com.ssafy.withview.controller;

import java.util.*;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/utils")
public class UtilController {
	private final Environment env;

	@GetMapping("/profile")
	public String getProfile(){
		System.out.println("get: " + Arrays.stream(env.getActiveProfiles()).findFirst().orElse(""));
		return Arrays.stream(env.getActiveProfiles()).findFirst().orElse("");
	}

	public void pushTest2() {
	}
}
