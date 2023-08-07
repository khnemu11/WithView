package com.ssafy.withview.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.ssafy.withview.entity.EmailVerificationCodeEntity;
import com.ssafy.withview.exception.InvalidVerificationCodeException;
import com.ssafy.withview.repository.EmailVerificationCodeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	private final AmazonSimpleEmailService amazonSimpleEmailService;

	private final EmailVerificationCodeRepository emailVerificationCodeRepository;

	static final String SOURCE_EMAIL = "withview208@gmail.com";

	/**
	 * 이메일로 인증코드 전송
	 *
	 * @param email (이메일)
	 */
	public void sendEmail(String email) {
		log.debug("EmailService - sendEmail 실행");

		Destination destination = new Destination().withToAddresses(email.trim());

		Message message = new Message()
			.withSubject(createContent("WithView 이메일 인증 코드"))
			.withBody(new Body()
				.withHtml(createContent(generateEmailVerificationCode(email.trim()))));

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

	/**
	 * 이메일 인증 코드 6자리 생성 및 Redis 저장 (3분)
	 *
	 * @param email (수신 email)
	 * @return String (인증 코드 6자리)
	 */
	public String generateEmailVerificationCode(String email) {
		log.debug("EmailService - generateEmailVerificationCode 실행");

		Random random = new Random();
		int randomNum = random.nextInt(999999);
		String emailVerificationCode = String.format("%06d", randomNum);

		// Redis 에 3분간 저장
		emailVerificationCodeRepository.save(new EmailVerificationCodeEntity(email, emailVerificationCode));
		log.debug("이메일 인증 코드 redis 저장 완료: {}", emailVerificationCode);

		return emailVerificationCode;
	}

	/**
	 * 전송한 인증 코드 검증
	 *
	 * @param email (수신 email)
	 * @param code  (인증코드 6자리)
	 * @return Boolean (true: 인증코드 일치, false: 인증코드 불일치 or 만료)
	 */
	public Boolean checkEmailVerificationCode(String email, String code) {
		log.debug("EmailService - checkEmailVerificationCode 실행");

		EmailVerificationCodeEntity emailVerificationCodeEntity = emailVerificationCodeRepository.findById(email)
			.orElseThrow(() -> new InvalidVerificationCodeException("Invalid code"));

		if (emailVerificationCodeEntity.getCode().equals(code)) {
			return true;
		}

		return false;
	}
}
