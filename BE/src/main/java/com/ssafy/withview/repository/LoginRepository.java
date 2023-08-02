package com.ssafy.withview.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.withview.entity.LoginEntity;

public interface LoginRepository extends JpaRepository<LoginEntity, Integer> {
	Optional<LoginEntity> findById(String id);
}
