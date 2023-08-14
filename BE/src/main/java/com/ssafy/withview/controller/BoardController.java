package com.ssafy.withview.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.dto.BoardDto;
import com.ssafy.withview.dto.LoginDto;
import com.ssafy.withview.exception.BadRequestException;
import com.ssafy.withview.service.BoardService;
import com.ssafy.withview.service.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

	private final BoardService boardService;
	private final JwtService jwtService;

	/**
	 * 게시글 작성
	 *
	 * @param boardDto (제목, 내용, 작성자 pk 값, 프리셋 pk 값)
	 * @return ResponseEntity (true / false, 상태코드)
	 * @throws IllegalArgumentException (작성자 pk 에 일치하는 회원 정보가 없을 때)
	 * @throws BadRequestException (로그인 유저와 작성자의 pk 값이 다를 때)
	 */
	@PostMapping("")
	public ResponseEntity<Map<String, Object>> insertBoardArticle(@RequestBody BoardDto boardDto,
		HttpServletRequest request) {
		log.debug("BoardController - writeBoardArticle: 게시글 작성");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			String accessToken = jwtService.resolveAccessToken(request.getHeader("Authorization"));
			log.debug("AccessToken: {}", accessToken);
			LoginDto loginDto = jwtService.getLoginInfo(accessToken);
			if (loginDto.getUserSeq() != boardDto.getUserSeq()) {
				throw new BadRequestException("BAD_REQUEST");
			}
			boardService.insertBoardArticle(boardDto);
			resultMap.put("success", true);
			status = HttpStatus.CREATED;
			log.debug("게시글 등록 성공, seq: {}", boardDto.getBoardSeq());
		} catch (IllegalArgumentException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.NOT_FOUND;
			log.info("[Error] 게시글 등록 실패, ErrorMessage: {}", e.getMessage());
		} catch (BadRequestException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.BAD_REQUEST;
			log.warn("[Error] 로그인 유저와 작성자 seq 값이 다름: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", "게시글 등록 실패");
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 게시글 등록 실패, ErrorMessage: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 게시글 목록
	 *
	 * @return ResponseEntity (true / false, 상태코드, BoardDto List - 게시글 pk 값, 제목, 작성자 닉네임, 프리셋 png)
	 */
	@GetMapping("")
	public ResponseEntity<Map<String, Object>> getBoardArticles() {
		log.debug("BoardController - getBoardArticles: 게시글 목록 확인");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			List<BoardDto> boardDtos = boardService.getBoardArticles();
			resultMap.put("BoardListInfo", boardDtos);
			resultMap.put("success", true);
			status = HttpStatus.OK;
			log.debug("게시글 목록 불러오기 성공");
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", "게시글 목록 불러오기 실패");
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 게시글 목록 불러오기 실패, ErrorMessage: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 게시글 상세 정보
	 *
	 * @param seq (게시글 seq)
	 * @return ResponseEntity (true / false, 상태코드, BoardDto - 게시글 pk 값, 제목, 내용, 등록일, 작성자 pk 값, 닉네임, 프로필 png, 프리셋 png, stage)
	 * @throws IllegalArgumentException (seq 에 일치하는 게시글이 없을 때)
	 */
	@GetMapping("/{seq}")
	public ResponseEntity<Map<String, Object>> getBoardArticle(@PathVariable(value = "seq") Long seq) {
		log.debug("BoardController - getBoardArticle: 게시글 상세 정보 확인");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			BoardDto boardDto = boardService.getBoardArticle(seq);
			resultMap.put("BoardInfo", boardDto);
			resultMap.put("success", true);
			status = HttpStatus.OK;
			log.debug("게시글 상세 정보 불러오기 성공");
		} catch (IllegalArgumentException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.NOT_FOUND;
			log.info("[Error] 게시글 상세 정보 불러오기 실패, ErrorMessage: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", "게시글 상세 정보 불러오기 실패");
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 게시글 상세 정보 불러오기 실패, ErrorMessage: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 게시글 수정
	 *
	 * @param boardDto (제목, 내용, 게시글 pk 값, 프리셋 pk 값)
	 * @return ResponseEntity (true / false, 상태코드)
	 * @throws BadRequestException (로그인 유저와 작성자의 pk 값이 다를 때)
	 */
	@PutMapping("")
	public ResponseEntity<Map<String, Object>> updateBoardArticle(@RequestBody BoardDto boardDto,
		HttpServletRequest request) {
		log.debug("BoardController - updateBoardArticle: 게시글 수정");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			String accessToken = jwtService.resolveAccessToken(request.getHeader("Authorization"));
			log.debug("AccessToken: {}", accessToken);
			LoginDto loginDto = jwtService.getLoginInfo(accessToken);
			if (loginDto.getUserSeq() != boardService.getUserSeq(boardDto.getBoardSeq())) {
				throw new BadRequestException("BAD_REQUEST");
			}
			boardService.updateBoardArticle(boardDto);
			resultMap.put("success", true);
			status = HttpStatus.OK;
			log.debug("게시글 수정 성공");
		} catch (BadRequestException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.BAD_REQUEST;
			log.warn("[Error] 로그인 유저와 작성자 seq 값이 다름: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", "게시글 수정 실패");
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 게시글 수정 실패, ErrorMessage: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 게시글 삭제
	 *
	 * @param seq (게시글 seq)
	 * @return ResponseEntity (true / false, 상태코드)
	 * @throws BadRequestException (로그인 유저와 작성자의 pk 값이 다를 때, seq 에 일치하는 게시글이 없을 때)
	 */
	@DeleteMapping("/{seq}")
	public ResponseEntity<Map<String, Object>> deleteBoardArticle(@PathVariable(value = "seq") Long seq,
		HttpServletRequest request) {
		log.info("BoardController - deleteBoardArticle: 게시글 삭제");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			String accessToken = jwtService.resolveAccessToken(request.getHeader("Authorization"));
			log.debug("AccessToken: {}", accessToken);
			LoginDto loginDto = jwtService.getLoginInfo(accessToken);
			if (loginDto.getUserSeq() != boardService.getUserSeq(seq)) {
				throw new BadRequestException("BAD_REQUEST");
			}
			boardService.deleteBoardArticle(seq);
			resultMap.put("success", true);
			status = HttpStatus.OK;
			log.debug("게시글 삭제 성공");
		} catch (BadRequestException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.NOT_FOUND;
			log.warn("[Error] 게시글 삭제 실패, ErrorMessage: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", "게시글 삭제 실패");
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 게시글 삭제 실패, ErrorMessage: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}
}
