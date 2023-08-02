package com.ssafy.withview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.entity.FavoriteEntity;
import com.ssafy.withview.entity.UserEntity;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity,Long> {
	public List<FavoriteEntity> findAllByUserEntity(UserEntity userEntity);
}