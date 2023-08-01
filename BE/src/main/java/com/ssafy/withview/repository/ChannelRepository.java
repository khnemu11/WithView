package com.ssafy.withview.repository;

import java.util.List;

import com.ssafy.withview.repository.entity.ServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.repository.entity.ChannelEntity;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity,Long> {
	public List<ChannelEntity> findAllChannelByServerEntity(ServerEntity serverEntity);
	public ChannelEntity findBySeq(long channelSeq);
}