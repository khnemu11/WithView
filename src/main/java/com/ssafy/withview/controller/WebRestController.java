package com.ssafy.withview.controller;

import java.util.*;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WebRestController {
	private final Environment env;

	@GetMapping("/profile")
	public String getProfile(){
		return Arrays.stream(env.getActiveProfiles()).findFirst().orElse("");
	}
}
