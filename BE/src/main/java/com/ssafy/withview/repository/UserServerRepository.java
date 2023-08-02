package com.ssafy.withview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.entity.ServerEntity;
import com.ssafy.withview.entity.UserEntity;
import com.ssafy.withview.entity.UserServerEntity;

@Repository
public interface UserServerRepository extends JpaRepository<UserServerEntity, Long> {
	List<UserServerEntity> findAllServerByUserEntity(UserEntity userEntity);

	List<UserServerEntity> findAllUserByServerEntity(ServerEntity userEntity);

	UserServerEntity findByServerEntityAndUserEntity(ServerEntity serverEntity, UserEntity userEntity);
}