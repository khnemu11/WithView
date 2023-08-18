package com.ssafy.withview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ssafy.withview.entity.PresetEntity;

public interface PresetRepository extends MongoRepository<PresetEntity, String> {

	Optional<PresetEntity> findById(String id);

	List<PresetEntity> findAllByUserSeq(Long userSeq);

	List<PresetEntity> findAllByUserSeqAndPresetName(Long UserSeq, String keyword);
}

