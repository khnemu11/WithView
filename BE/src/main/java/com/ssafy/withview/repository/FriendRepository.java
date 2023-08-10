package com.ssafy.withview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.entity.FriendEntity;

@Repository
public interface FriendRepository extends JpaRepository<FriendEntity, Long> {
	public List<FriendEntity> findAllByFollowingUserSeq(Long followingUserSeq);
}
