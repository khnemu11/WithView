package com.ssafy.withview.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

	NON_LOGIN("false", "NON_LOGIN"),
	EXPIRED_TOKEN("false", "EXPIRED_TOKEN"),
	ACCESS_DENIED("false", "ACCESS_DENIED");

	private final String success;
	private final String message;
}
