package com.ssafy.withview.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.entity.FriendsChatMessageEntity;

@Repository
public interface FriendsChatMessageRepository extends MongoRepository<FriendsChatMessageEntity, Long> {
	public List<FriendsChatMessageEntity> findByFriendsChatRoomSeqOrderByMessageSeqDesc(Long friendsChatRoomSeq,
		Pageable pageable);

	public FriendsChatMessageEntity findTopByFriendsChatRoomSeqOrderByMessageSeqDesc(Long friendsChatRoomSeq);
}
