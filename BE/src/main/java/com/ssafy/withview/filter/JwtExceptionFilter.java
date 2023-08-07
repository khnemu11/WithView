package com.ssafy.withview.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.withview.constant.ErrorCode;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		log.debug("JwtExceptionFilter 실행");

		try {
			filterChain.doFilter(request, response);
		} catch (JwtException exception) {
			setErrorResponse(request, response, exception);
		}
	}

	public void setErrorResponse(HttpServletRequest request, HttpServletResponse response, Throwable exception) throws
		IOException {
		log.debug("JwtExceptionFilter - setErrorResponse 실행");

		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

		if (exception.getMessage().equals("INVALID_TOKEN")) {
			errorCode = ErrorCode.INVALID_TOKEN;
		}

		if (exception.getMessage().equals("EXPIRED_REFRESH_TOKEN")) {
			errorCode = ErrorCode.EXPIRED_REFRESH_TOKEN;
		}

		if (exception.getMessage().equals("REISSUE_ACCESS_TOKEN")) {
			errorCode = ErrorCode.REISSUE_ACCESS_TOKEN;
		}

		String responseBody = objectMapper.writeValueAsString(errorCode);

		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(errorCode.getCode());
		response.getWriter().println(responseBody);
	}
}
