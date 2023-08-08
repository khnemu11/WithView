package com.ssafy.withview.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserEntityTest {

	@Test
	@DisplayName("UserEntity 생성 확인 테스트")
	public void createUserEntity() {
		// given
		UserEntity userEntity = UserEntity.builder()
			.id("testId")
			.email("testEmail")
			.nickname("testNickname")
			.build();

		// when, then
		Assertions.assertThat(userEntity.getId()).isEqualTo("testId");
		Assertions.assertThat(userEntity.getEmail()).isEqualTo("testEmail");
		Assertions.assertThat(userEntity.getNickname()).isEqualTo("testNickname");
	}
}