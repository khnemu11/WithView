package com.ssafy.withview.repository;

import java.util.List;

import com.ssafy.withview.entity.ServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.entity.ChannelEntity;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity,Long> {
	public List<ChannelEntity> findAllChannelByServerEntity(ServerEntity serverEntity);
	public ChannelEntity findBySeq(long channelSeq);
}