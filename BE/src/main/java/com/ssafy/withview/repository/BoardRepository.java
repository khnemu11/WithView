package com.ssafy.withview.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.entity.BoardEntity;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

	Optional<BoardEntity> findBySeq(Long seq);

	Boolean existsBySeq(Long seq);

	void deleteBySeq(Long seq);
}
