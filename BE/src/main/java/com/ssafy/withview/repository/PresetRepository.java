package com.ssafy.withview.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ssafy.withview.entity.PresetEntity;

public interface PresetRepository extends MongoRepository<PresetEntity, String> {
}
