package com.ssafy.withview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.repository.entity.ChannelEntity;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity,Long> {
	public List<ChannelEntity> findAllByServerSeq(long serverSeq);
}