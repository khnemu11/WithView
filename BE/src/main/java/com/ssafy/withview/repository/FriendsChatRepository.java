package com.ssafy.withview.repository;

import com.ssafy.withview.entity.ChannelChatEntity;
import com.ssafy.withview.entity.FriendsChatEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendsChatRepository extends MongoRepository<FriendsChatEntity, String> {
    public List<FriendsChatEntity> findByFriendsChatSeqOrderBySendTimeDesc(Long friendsChatSeq, Pageable pageable);
}
