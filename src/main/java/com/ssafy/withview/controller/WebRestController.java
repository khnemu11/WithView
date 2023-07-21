package com.ssafy.withview.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@PropertySource(value = "application.yml")
public class WebRestController {
	private final Environment env;

	@GetMapping("/profile")
	public String getProfile(){
		return Arrays.stream(env.getActiveProfiles()).findFirst().orElse("");
	}
}
