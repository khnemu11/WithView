package com.ssafy.withview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.entity.ChannelEntity;
import com.ssafy.withview.entity.ServerEntity;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {
	public List<ChannelEntity> findAllChannelByServerEntity(ServerEntity serverEntity);

	public ChannelEntity findBySeq(Long channelSeq);

	public List<ChannelEntity> findByServerEntitySeq(Long serverSeq);
}