package com.ssafy.withview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.repository.entity.FavoriteEntity;
import com.ssafy.withview.repository.entity.ServerEntity;
import com.ssafy.withview.repository.entity.UserEntity;
import com.ssafy.withview.repository.entity.UserServerEntity;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity,Long> {
	public List<FavoriteEntity> findAllByUserEntity(UserEntity userEntity);
}