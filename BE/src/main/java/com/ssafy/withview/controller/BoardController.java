package com.ssafy.withview.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.dto.BoardDto;
import com.ssafy.withview.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

	private final BoardService boardService;

	/**
	 * 게시글 작성
	 *
	 * @param boardDto (작성자 pk 값, 제목, 내용, 프리셋 이름)
	 * @return ResponseEntity (true / false, 상태코드)
	 */
	@PostMapping("")
	public ResponseEntity<Map<String, Object>> writeBoardArticle(@RequestBody BoardDto boardDto) {
		log.debug("BoardController - writeBoardArticle: 게시글 작성");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			boardService.writeBoardArticle(boardDto);
			resultMap.put("success", "true");
			status = HttpStatus.CREATED;
			log.debug("게시글 등록 성공, seq: {}", boardDto.getSeq());
		} catch (Exception e) {
			resultMap.put("success", "false");
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.debug("게시글 등록 실패, ErrorMessage: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}
}
