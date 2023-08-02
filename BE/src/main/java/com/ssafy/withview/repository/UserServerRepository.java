package com.ssafy.withview.repository;

import com.ssafy.withview.entity.ServerEntity;
import com.ssafy.withview.entity.UserEntity;
import com.ssafy.withview.entity.UserServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserServerRepository extends JpaRepository<UserServerEntity,Long> {
    List<UserServerEntity> findAllServerByUserEntity(UserEntity userEntity);
    List<UserServerEntity> findAllUserByServerEntity(ServerEntity userEntity);
    UserServerEntity findByServerEntityAndUserEntity(ServerEntity serverEntity,UserEntity userEntity);
}