package com.ssafy.withview.repository;

import org.springframework.data.repository.CrudRepository;

import com.ssafy.withview.entity.EmailVerificationCodeEntity;

public interface EmailVerificationCodeRepository extends CrudRepository<EmailVerificationCodeEntity, String> {
}
