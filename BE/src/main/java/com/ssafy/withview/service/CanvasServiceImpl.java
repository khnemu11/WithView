package com.ssafy.withview.service;

import com.ssafy.withview.repository.CanvasRepository;
import com.ssafy.withview.dto.CanvasDto;
import com.ssafy.withview.entity.CanvasEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.transaction.Transactional;

@Service
public class CanvasServiceImpl implements CanvasService{
	@Autowired
	private CanvasRepository canvasRepository;

	@Override
	public void insertCanvas(CanvasDto canvasDto) {
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

		return CanvasEntity.toDto(canvasEntityList.get(0));
	}
}
