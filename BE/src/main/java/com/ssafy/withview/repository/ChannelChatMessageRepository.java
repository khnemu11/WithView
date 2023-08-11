package com.ssafy.withview.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.entity.ChannelChatMessageEntity;

@Repository
public interface ChannelChatMessageRepository extends MongoRepository<ChannelChatMessageEntity, Long> {
	public List<ChannelChatMessageEntity> findByChannelSeqOrderBySendTimeDesc(Long channelSeq, Pageable pageable);
}
