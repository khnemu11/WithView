package com.ssafy.withview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("http://localhost:5173");
		config.addAllowedOrigin("http://localhost:5174");
		config.addAllowedOrigin("https://i9D208.p.ssafy.io");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.addExposedHeader("Grant-Type");
		config.addExposedHeader("New-Access-Token");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return new CorsFilter(source);
	}
}
