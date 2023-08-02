package com.ssafy.withview.exception;

public class InvalidVerificationCodeException extends RuntimeException {
	public InvalidVerificationCodeException(String msg) {
		super(msg);
	}
}
