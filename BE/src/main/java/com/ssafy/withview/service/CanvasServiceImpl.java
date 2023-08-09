package com.ssafy.withview.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.withview.dto.CanvasMessageDto;
import com.ssafy.withview.repository.CanvasRepository;
import com.ssafy.withview.dto.CanvasDto;
import com.ssafy.withview.entity.CanvasEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CanvasServiceImpl implements CanvasService{
	@Autowired
	private CanvasRepository canvasRepository;
	@Autowired
	private ChannelTopic canvasTopic;
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public void insertCanvas(CanvasDto canvasDto) {
		log.info("입력받은 캔버스 dto",canvasDto.toString());
		CanvasEntity canvasEntity = CanvasDto.toEntity(canvasDto);
		canvasRepository.save(canvasEntity);
	}

	@Transactional
	@Override
	public void updateCanvas(CanvasDto canvasDto) {
		List<CanvasEntity> canvasEntityList = canvasRepository.findByChannelSeq(canvasDto.getChannelSeq());
		CanvasEntity canvasEntity = canvasEntityList.get(0);
		canvasEntity.update(canvasDto);

		canvasRepository.save(canvasEntity);
	}

	@Transactional
	@Override
	public void deleteCanvasByChannelSeq(Long channelSeq) {
		List<CanvasEntity> canvasEntityList = canvasRepository.findByChannelSeq(channelSeq);
		CanvasEntity canvasEntity = canvasEntityList.get(0);

		canvasRepository.delete(canvasEntity);
	}

	@Override
	public CanvasDto findCanvasByChannelSeq(Long channelSeq) {
		List<CanvasEntity> canvasEntityList = canvasRepository.findByChannelSeq(channelSeq);
		
		log.info(canvasEntityList.get(0).toString());

		log.info(canvasEntityList.get(0).toString());

		return CanvasEntity.toDto(canvasEntityList.get(0));
	}

	@Override
	public void sendCanvasMessage(String message) throws JsonProcessingException {
		log.info("캔버스 메시지 전송");
		log.info(message);
		ObjectMapper objectMapper = new ObjectMapper();
		CanvasMessageDto canvasDto = objectMapper.readValue(message, CanvasMessageDto.class);
		CanvasEntity canvasEntity = CanvasDto.toEntity(canvasDto);

		redisTemplate.convertAndSend(canvasTopic.getTopic(), message);
	}
}
