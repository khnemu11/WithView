package com.ssafy.withview.repository;

import com.ssafy.withview.repository.entity.CanvasEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CanvasRepository extends MongoRepository<CanvasEntity,String> {
    public List<CanvasEntity> findByChannelSeq(long channelSeq);
}