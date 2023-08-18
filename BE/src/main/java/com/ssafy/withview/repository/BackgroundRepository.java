package com.ssafy.withview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.entity.BackgroundEntity;
import com.ssafy.withview.entity.StickerEntity;

@Repository
public interface BackgroundRepository extends JpaRepository<BackgroundEntity, Long> {
	public List<BackgroundEntity> findAllByOriginalNameContains(String keyword);
	public BackgroundEntity findBySeq(Long seq);
	public List<BackgroundEntity> findAllByUserSeq(Long userSeq);
	public List<BackgroundEntity> findAllByUserSeqAndOriginalNameContains(Long userSeq,String keyword);
}