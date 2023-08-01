package com.ssafy.withview.service;

import org.springframework.stereotype.Service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	private final AmazonSimpleEmailService amazonSimpleEmailService;

	static final String SOURCE_EMAIL = "withview208@gmail.com";

	public void sendEmail(String toAddress) {
		log.info("EmailService - sendEmail 실행");

		Destination destination = new Destination().withToAddresses(toAddress.trim());

		Message message = new Message()
			.withSubject(createContent("인증 코드입니다."))
			.withBody(new Body()
				.withHtml(createContent(generateEmailVerificationCode(toAddress.trim()))));

		SendEmailResult sendEmailResult = amazonSimpleEmailService.sendEmail(new SendEmailRequest()
			.withSource(SOURCE_EMAIL)
			.withDestination(destination)
			.withMessage(message));
	}

	private Content createContent(String text) {
		return new Content()
			.withCharset("UTF-8")
			.withData(text);
	}

	public String generateEmailVerificationCode(String email) {
		log.info("EmailService - generateEmailVerificationCode 실행");
		// 1. 인증코드 6자리 생성
		// 2. redis 에 3분간 저장 (key: email, value: code)
		return "123456";
	}

	public boolean checkEmailVerificationCode(String email, String code) {
		log.info("EmailService - checkEmailVerificationCode 실행");
		// 1. redis 에 key: email 존재 여부 확인
		// 2. 해당 code 가져온 후 값 비교
		if (code.equals("123456")) {
			return true;
		} else {
			return false;
		}
	}
}
