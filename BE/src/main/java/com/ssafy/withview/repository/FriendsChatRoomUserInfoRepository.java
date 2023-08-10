package com.ssafy.withview.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.withview.entity.FriendsChatRoomUserInfoEntity;

public interface FriendsChatRoomUserInfoRepository extends JpaRepository<FriendsChatRoomUserInfoEntity, Long> {
	public Set<FriendsChatRoomUserInfoEntity> findAllByUserSeq(Long userSeq);

	public FriendsChatRoomUserInfoEntity findBySeqAndUserSeqNot(Long seq, Long userSeq);

	public FriendsChatRoomUserInfoEntity findTopByFriendsChatRoomEntitySeqAndUserSeq(
		Long friendsChatRoomSeq,
		Long userSeq);
}
