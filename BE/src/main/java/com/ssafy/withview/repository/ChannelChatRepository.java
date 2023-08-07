package com.ssafy.withview.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.entity.ChannelChatEntity;

@Repository
public interface ChannelChatRepository extends MongoRepository<ChannelChatEntity, String> {
	public List<ChannelChatEntity> findByChannelSeqOrderBySendTimeDesc(Long channelSeq, Pageable pageable);
}
