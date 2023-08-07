package com.ssafy.withview.repository;

import com.ssafy.withview.entity.CanvasEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanvasRepository extends MongoRepository<CanvasEntity,String> {
    public List<CanvasEntity> findByChannelSeq(Long channelSeq);
}