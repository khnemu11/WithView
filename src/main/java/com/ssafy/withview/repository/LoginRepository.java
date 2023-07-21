package com.ssafy.withview.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.withview.repository.dto.LoginDto;

public interface LoginRepository extends JpaRepository<LoginDto, Integer> {

	Optional<LoginDto> findById(String id);
}
