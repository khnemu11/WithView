package com.ssafy.withview.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.withview.dto.BoardDto;
import com.ssafy.withview.entity.BoardEntity;
import com.ssafy.withview.entity.UserEntity;
import com.ssafy.withview.exception.BadRequestException;
import com.ssafy.withview.repository.BoardRepository;
import com.ssafy.withview.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

	private final BoardRepository boardRepository;

	private final UserRepository userRepository;

	/**
	 * 게시글 작성
	 *
	 * @param boardDto (작성자 pk 값, 제목, 내용, 프리셋 이름)
	 */
	@Transactional
	public void insertBoardArticle(BoardDto boardDto) {
		log.debug("BoardService - insertBoardArticle 실행");

		UserEntity userEntity = userRepository.findBySeq(boardDto.getUserSeq())
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		boardRepository.save(BoardEntity.builder()
			.userSeq(boardDto.getUserSeq())
			.nickname(userEntity.getNickname())
			.title(boardDto.getTitle())
			.content(boardDto.getContent())
			.presetImgSearchName(boardDto.getPresetImgSearchName())
			.build());
	}

	/**
	 * 게시글 목록
	 *
	 * @return BoardDto List (게시글 pk 값, 작성자 닉네임, 제목, 내용, 등록일)
	 */
	@Transactional
	public List<BoardDto> getBoardArticles() {
		log.debug("BoardService - getBoardArticles 실행");

		return boardRepository.findAll().stream()
			.map(b -> BoardDto.builder()
				.seq(b.getSeq())
				.nickname(b.getNickname())
				.title(b.getTitle())
				.content(b.getContent())
				.presetImgSearchName(b.getPresetImgSearchName())
				.registerTime(b.getRegisterTime())
				.build())
			.collect(Collectors.toList());
	}

	/**
	 * 게시글 상세 정보
	 *
	 * @param seq (게시글 seq)
	 * @return BoardDto (작성자 닉네임, 제목, 내용, 등록일, 이미지 png 이름)
	 */
	@Transactional
	public BoardDto getBoardArticle(Long seq) {
		log.debug("BoardService - getBoardArticle 실행");

		BoardEntity boardEntity = boardRepository.findBySeq(seq)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 게시글이 없습니다."));

		UserEntity userEntity = userRepository.findBySeq(boardEntity.getUserSeq())
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원정보가 없습니다."));

		return BoardDto.builder()
			.seq(boardEntity.getSeq())
			.userSeq(boardEntity.getUserSeq())
			.profileImgSearchName(userEntity.getProfileImgSearchName())
			.nickname(boardEntity.getNickname())
			.title(boardEntity.getTitle())
			.content(boardEntity.getContent())
			.registerTime(boardEntity.getRegisterTime())
			.presetImgSearchName(boardEntity.getPresetImgSearchName())
			.build();
	}

	/**
	 * 게시글 수정
	 *
	 * @param boardDto (게시글 pk 값, 제목, 내용, 프리셋 이름)
	 */
	@Transactional
	public void updateBoardArticle(BoardDto boardDto) {
		log.debug("BoardService - updateBoardArticle 실행");

		BoardEntity boardEntity = boardRepository.findBySeq(boardDto.getSeq())
			.orElseThrow(() -> new IllegalArgumentException("일치하는 게시글 정보가 없습니다."));

		boardEntity.updateBoardArticle(boardDto.getTitle(), boardDto.getContent(), boardDto.getPresetImgSearchName());
	}

	/**
	 * 게시글 삭제
	 *
	 * @param seq (삭제할 게시글 seq)
	 */
	@Transactional
	public void deleteBoardArticle(Long seq) {
		log.debug("BoardService - deleteBoardArticle 실행");

		if (!boardRepository.existsBySeq(seq)) {
			throw new BadRequestException("일치하는 게시글이 없습니다.");
		}

		boardRepository.deleteBySeq(seq);
	}

	/**
	 * 게시글 작성자 pk 값 얻기
	 *
	 * @param seq (게시글 pk 값)
	 * @return Long (작성자 pk 값)
	 */
	@Transactional
	public Long getUserSeq(Long seq) {
		log.debug("BoardService - getUserSeq 실행");

		if (!boardRepository.existsBySeq(seq)) {
			throw new BadRequestException("일치하는 게시글이 없습니다.");
		}

		return boardRepository.findBySeq(seq).get().getUserSeq();
	}
}
