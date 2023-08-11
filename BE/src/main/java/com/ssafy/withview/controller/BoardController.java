package com.ssafy.withview.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	 * @throws IllegalArgumentException (작성자 pk 에 일치하는 회원 정보가 없을 때)
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
		} catch (IllegalArgumentException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.BAD_REQUEST;
			log.info("[Error] 게시글 등록 실패, ErrorMessage: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", "false");
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 게시글 등록 실패, ErrorMessage: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 게시글 목록
	 *
	 * @return ResponseEntity (true / false, 상태코드, BoardDto List - 게시글 pk 값, 작성자 닉네임, 제목, 내용, 등록일)
	 */
	@GetMapping("")
	public ResponseEntity<Map<String, Object>> getBoardArticles() {
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			List<BoardDto> boardDtos = boardService.getBoardArticles();
			resultMap.put("BoardListInfo", boardDtos);
			resultMap.put("success", "true");
			status = HttpStatus.OK;
			log.debug("게시글 목록 불러오기 성공");
		} catch (Exception e) {
			resultMap.put("success", "false");
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 게시글 목록 불러오기 실패, ErrorMessage: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 게시글 상세 정보
	 *
	 * @param seq (게시글 seq)
	 * @return ResponseEntity (true / false, 상태코드, BoardDto - 작성자 닉네임, 제목, 내용, 등록일, 이미지 png 이름)
	 * @throws IllegalArgumentException (seq 에 일치하는 게시글이 없을 때)
	 */
	@GetMapping("/{seq}")
	public ResponseEntity<Map<String, Object>> getBoardArticle(@PathVariable(value = "seq") Long seq) {
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			BoardDto boardDto = boardService.getBoardArticle(seq);
			resultMap.put("BoardInfo", boardDto);
			resultMap.put("success", "true");
			status = HttpStatus.OK;
			log.debug("게시글 상세 정보 불러오기 성공");
		} catch (IllegalArgumentException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.BAD_REQUEST;
			log.info("[Error] 게시글 상세 정보 불러오기 실패, ErrorMessage: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", "false");
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 게시글 상세 정보 불러오기 실패, ErrorMessage: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 게시글 수정
	 *
	 * @param boardDto (작성자 pk 값, 제목, 내용, 프리셋 이름)
	 * @return ResponseEntity (true / false, 상태코드)
	 * @throws IllegalArgumentException (작성자 pk 에 일치하는 회원 정보가 없을 때)
	 */
	@PutMapping("")
	public ResponseEntity<Map<String, Object>> updateBoardArticle(@RequestBody BoardDto boardDto) {
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			boardService.writeBoardArticle(boardDto);
			resultMap.put("success", "true");
			status = HttpStatus.OK;
			log.debug("게시글 수정 성공");
		} catch (IllegalArgumentException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.BAD_REQUEST;
			log.info("[Error] 게시글 수정 실패, ErrorMessage: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", "false");
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 게시글 수정 실패, ErrorMessage: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}
}
