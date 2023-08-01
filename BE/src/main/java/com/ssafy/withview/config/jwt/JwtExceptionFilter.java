package com.ssafy.withview.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.withview.exception.ErrorCode;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

// @Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtExceptionFilter.class);

	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		logger.info("JwtExceptionFilter 실행");

		try {
			filterChain.doFilter(request, response);
		} catch (JwtException exception) {
			setErrorResponse(request, response, exception);
		}
	}

	public void setErrorResponse(HttpServletRequest request, HttpServletResponse response, Throwable exception) throws
		IOException {

		ErrorCode errorCode;

		if (exception.getMessage().equals("NON_LOGIN")) {
			errorCode = ErrorCode.NON_LOGIN;
		} else {
			errorCode = ErrorCode.EXPIRED_TOKEN;
		}

		String responseBody = objectMapper.writeValueAsString(errorCode);

		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().println(responseBody);
	}
}
