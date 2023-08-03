package com.ssafy.withview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.withview.entity.FriendEntity;

public interface FriendRepository extends JpaRepository<FriendEntity, Long> {
}
