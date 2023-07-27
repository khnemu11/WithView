package com.ssafy.withview.config.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.withview.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {
		logger.info("JwtAuthenticationEntryPoint 실행");

		String exception = (String)request.getAttribute("exception");
		ErrorCode errorCode;

		logger.info("log: exception: {} ", exception);

		/**
		 * 토큰 없는 경우
		 */
		if (exception == null) {
			errorCode = ErrorCode.NON_LOGIN;
			setResponse(response, errorCode);
			return;
		}

		/**
		 * 토큰 만료된 경우
		 */
		if (exception.equals("401")) {
			errorCode = ErrorCode.EXPIRED_TOKEN;
			setResponse(response, errorCode);
		}
	}

	/**
	 * 한글 출력을 위해 getWriter() 사용
	 */
	private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {

		String responseBody = objectMapper.writeValueAsString(errorCode);
		logger.info(responseBody);
		
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().println(responseBody);
	}

}
