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

		StringBuilder sb = new StringBuilder();
		sb.append("<div>");
		sb.append("<span style='white-space:nowrap'>안녕하세요.&nbsp;</span>");
		sb.append("<span style='font-weight:bold;white-space:nowrap'>WithView</span><span> 입니다.</span>");
		sb.append("<br>");
		sb.append("<br>");
		sb.append("<span>아래  인증코드를 회원가입 창으로 돌아가 입력해주세요</span>");
		sb.append("<br>");
		sb.append("<br>");
		sb.append(
			"<span style='color:#39CCCC;font-size:24px;font-weight:bold'>" + generateEmailVerificationCode(email.trim())
				+ "</span><br>"); // 메일에 인증번호 넣기
		sb.append("<br>");
		sb.append("<br>");
		sb.append("<span style='font-weight:bold'>※주의 : </span><span>인증번호는 "
			+ "</span><span style='font-weight:bold'>3분 이후에 만료&nbsp;</span>"
			+ "<span>되므로 꼭 3분 이내에 입력해주시길 바랍니다.</span>");
		sb.append("<br>");
		sb.append("<br>");
		sb.append("<p>감사합니다!<p>");

		Message message = new Message()
			.withSubject(createContent("WithView 이메일 인증 코드"))
			.withBody(new Body()
				.withHtml(createContent(sb.toString())));

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
