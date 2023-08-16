package com.ssafy.withview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.entity.ChannelEntity;
import com.ssafy.withview.entity.ServerEntity;
import com.ssafy.withview.entity.StickerEntity;

@Repository
public interface StickerRepository extends JpaRepository<StickerEntity, Long> {
	public List<StickerEntity> findAllByOriginalNameContains(String keyword);
	public StickerEntity findBySeq(Long stickerSeq);
	public List<StickerEntity> findAllByUserSeq(Long userSeq);
	public List<StickerEntity> findAllByUserSeqAndOriginalNameContains(Long userSeq,String keyword);
}