package com.ssafy.withview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.withview.repository.entity.LoginEntity;

public interface UserRepository extends JpaRepository<LoginEntity, Integer> {

}
