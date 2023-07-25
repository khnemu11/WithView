package com.ssafy.withview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class WithviewApplication {

	public static void main(String[] args) {
		SpringApplication.run(WithviewApplication.class, args);
	}

}
