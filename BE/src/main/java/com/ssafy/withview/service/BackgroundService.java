package com.ssafy.withview.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ssafy.withview.dto.BackgroundDto;
import com.ssafy.withview.dto.StickerDto;

public interface BackgroundService {
	public BackgroundDto insertBackground(BackgroundDto backgroundDto, MultipartFile file) throws Exception;

	public void deleteBackground(Long seq) throws Exception;

	public List<BackgroundDto> findAllBackgrounds();

	public List<BackgroundDto> findAllBackgroundsByName(String keyword);

	public List<BackgroundDto> findAllBackgroundsByUserSeq(BackgroundDto backgroundDto);
}

