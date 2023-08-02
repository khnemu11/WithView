package com.ssafy.withview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ssafy.withview.entity.ServerEntity;

@Repository
public interface ServerRepository extends JpaRepository<ServerEntity,Long> {
	public ServerEntity findBySeq(long serverSeq);
	// public ChannelEntity findChannel(String channelName);
}