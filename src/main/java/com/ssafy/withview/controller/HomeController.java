package com.ssafy.withview.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

	@ResponseBody
	@GetMapping("/api/tmp")
	public ResponseEntity<String> index() {
		System.out.println("hello");
		return new ResponseEntity<String>("hello", HttpStatus.OK);
	}
}
