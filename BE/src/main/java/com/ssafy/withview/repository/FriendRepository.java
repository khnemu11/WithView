package com.ssafy.withview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.withview.entity.FriendEntity;

import java.util.List;

public interface FriendRepository extends JpaRepository<FriendEntity, Long> {
    public List<FriendEntity> findAllByFollowingUserSeq(Long followingUserSeq);
}
