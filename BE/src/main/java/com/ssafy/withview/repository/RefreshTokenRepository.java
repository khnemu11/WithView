package com.ssafy.withview.repository;

import org.springframework.data.repository.CrudRepository;

import com.ssafy.withview.entity.RefreshTokenEntity;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, String> {

}
