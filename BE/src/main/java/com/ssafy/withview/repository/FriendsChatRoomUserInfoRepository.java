package com.ssafy.withview.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.entity.FriendsChatRoomEntity;
import com.ssafy.withview.entity.FriendsChatRoomUserInfoEntity;

@Repository
public interface FriendsChatRoomUserInfoRepository extends JpaRepository<FriendsChatRoomUserInfoEntity, Long> {
	public Set<FriendsChatRoomUserInfoEntity> findAllByUserSeq(Long userSeq);

	public FriendsChatRoomUserInfoEntity findTopByFriendsChatRoomEntityAndUserSeqNot(FriendsChatRoomEntity entity, Long userSeq);

	public FriendsChatRoomUserInfoEntity findTopByFriendsChatRoomEntitySeqAndUserSeq(
		Long friendsChatRoomSeq,
		Long userSeq);

	@Query("SELECT table1 FROM FriendsChatRoomUserInfoEntity table1 " +
		"LEFT JOIN FriendsChatRoomUserInfoEntity table2 " +
		"ON table1.friendsChatRoomEntity = table2.friendsChatRoomEntity " +
		"WHERE table1.userSeq = :user1Seq AND table2.userSeq = :user2Seq")
	List<FriendsChatRoomUserInfoEntity> findFriendsChatRoomAlreadyExist(@Param("user1Seq") Long user1Seq, @Param("user2Seq") Long user2Seq);
}
