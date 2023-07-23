package com.ssafy.withview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.withview.repository.entity.LoginEntity;

public interface LoginRepository extends JpaRepository<LoginEntity, Integer> {

	// Optional<LoginEntity> findById(String id);
	LoginEntity findById(String id);

	LoginEntity findBySeq(Integer seq);
}
