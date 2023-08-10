package com.ssafy.withview.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.withview.dto.CanvasDto;
import com.ssafy.withview.dto.StickerDto;

public interface StickerService {
	public StickerDto insertSticker(StickerDto stickerDto, MultipartFile file) throws Exception;
	public void deleteSticker(Long seq) throws Exception;
	public List<StickerDto> findAllStickers();
	public List<StickerDto> findAllStickersByName(String keyword);
}
