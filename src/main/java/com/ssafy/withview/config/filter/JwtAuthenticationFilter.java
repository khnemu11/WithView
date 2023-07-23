package com.ssafy.withview.config.filter;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.withview.config.auth.PrincipalDetails;
import com.ssafy.withview.repository.entity.LoginEntity;

import lombok.RequiredArgsConstructor;

// POST 방식으로 login 요청을 하면, UsernamePasswordAuthenticationFilter 실행
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {

		ObjectMapper om = new ObjectMapper();
		LoginEntity loginEntity = null;

		try {
			loginEntity = om.readValue(request.getInputStream(), LoginEntity.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		System.out.println(loginEntity);

		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(loginEntity.getId(),
				bCryptPasswordEncoder.encode(loginEntity.getPassword()));

		Authentication authentication =
			authenticationManager.authenticate(authenticationToken);

		System.out.println(authenticationToken);

		PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();

		return authenticationToken;
	}

	// attemptAuthentication 실행 후 정상적으로 인증이 되면 successfulAuthentication 실행
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);

		PrincipalDetails principalDetails = (PrincipalDetails)authResult.getPrincipal();

		String accessToken = JWT.create()
			.withSubject(principalDetails.getUsername())
			.withExpiresAt(new Date(System.currentTimeMillis() + (JwtProperties.EXPIRATION_TIME_ACCESS)))
			.withClaim("iss", "S09P12D208")
			.withClaim("sub", "AccessToken")
			.withClaim("aud", principalDetails.getUsername())
			.withClaim("exp", JwtProperties.EXPIRATION_TIME_ACCESS)
			.sign(Algorithm.HMAC512(JwtProperties.SECRET));

		String refreshToken = JWT.create()
			.withSubject(principalDetails.getUsername())
			.withExpiresAt(new Date(System.currentTimeMillis() + (JwtProperties.EXPIRATION_TIME_REFRESH)))
			.withClaim("iss", "S09P12D208")
			.withClaim("sub", "RefreshToken")
			.withClaim("aud", principalDetails.getUsername())
			.withClaim("exp", JwtProperties.EXPIRATION_TIME_REFRESH)
			.sign(Algorithm.HMAC512(JwtProperties.SECRET));

		response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + accessToken);
		response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + refreshToken);
	}
}
