package com.ssafy.withview.repository;

import com.ssafy.withview.repository.entity.ServerEntity;
import com.ssafy.withview.repository.entity.UserServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserServerRepository extends JpaRepository<UserServerEntity,Long> {
}