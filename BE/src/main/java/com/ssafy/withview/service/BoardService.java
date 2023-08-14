package com.ssafy.withview.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.withview.dto.BoardDto;
import com.ssafy.withview.entity.BoardEntity;
import com.ssafy.withview.entity.PresetEntity;
import com.ssafy.withview.entity.UserEntity;
import com.ssafy.withview.exception.BadRequestException;
import com.ssafy.withview.repository.BoardRepository;
import com.ssafy.withview.repository.PresetRepository;
import com.ssafy.withview.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

	private final BoardRepository boardRepository;

	private final UserRepository userRepository;

	private final PresetRepository presetRepository;

	/**
	 * 게시글 작성
	 *
	 * @param boardDto (제목, 내용, 작성자 pk 값, 프리셋 pk 값)
	 */
	@Transactional
	public void insertBoardArticle(BoardDto boardDto) {
		log.debug("BoardService - insertBoardArticle 실행");

		UserEntity userEntity = userRepository.findBySeq(boardDto.getUserSeq())
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		PresetEntity presetEntity = presetRepository.findById(boardDto.getPresetId())
			.orElseThrow(() -> new IllegalArgumentException("일치하는 프리셋 정보가 없습니다."));

		boardRepository.save(BoardEntity.builder()
			.title(boardDto.getTitle())
			.content(boardDto.getContent())
			.userSeq(boardDto.getUserSeq())
			.nickname(userEntity.getNickname())
			.presetId(boardDto.getPresetId())
			.presetImgSearchName(presetEntity.getPresetImgSearchName())
			.build());
	}

	/**
	 * 게시글 목록
	 *
	 * @return BoardDto List (게시글 pk 값, 제목, 작성자 닉네임, 프리셋 png)
	 */
	@Transactional
	public List<BoardDto> getBoardArticles() {
		log.debug("BoardService - getBoardArticles 실행");

		return boardRepository.findAll().stream()
			.map(b -> BoardDto.builder()
				.boardSeq(b.getSeq())
				.title(b.getTitle())
				.nickname(b.getNickname())
				.presetImgSearchName(b.getPresetImgSearchName())
				.build())
			.collect(Collectors.toList());
	}

	/**
	 * 게시글 상세 정보
	 *
	 * @param seq (게시글 seq)
	 * @return BoardDto (게시글 pk 값, 제목, 내용, 등록일, 작성자 pk 값, 닉네임, 프로필 png, 프리셋 png, stage)
	 */
	@Transactional
	public BoardDto getBoardArticle(Long seq) {
		log.debug("BoardService - getBoardArticle 실행");

		BoardEntity boardEntity = boardRepository.findBySeq(seq)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 게시글이 없습니다."));

		UserEntity userEntity = userRepository.findBySeq(boardEntity.getUserSeq())
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원정보가 없습니다."));

		PresetEntity presetEntity = presetRepository.findById(boardEntity.getPresetId())
			.orElseThrow(() -> new IllegalArgumentException("일치하는 프리셋 정보가 없습니다."));

		return BoardDto.builder()
			.boardSeq(boardEntity.getSeq())
			.title(boardEntity.getTitle())
			.content(boardEntity.getContent())
			.registerTime(boardEntity.getRegisterTime())
			.userSeq(boardEntity.getUserSeq())
			.nickname(boardEntity.getNickname())
			.profileImgSearchName(userEntity.getProfileImgSearchName())
			.presetImgSearchName(boardEntity.getPresetImgSearchName())
			.stage(presetEntity.getStage())
			.build();
	}

	/**
	 * 게시글 수정
	 *
	 * @param boardDto (제목, 내용, 작성자 pk 값, 게시글 pk 값, 프리셋 pk 값)
	 */
	@Transactional
	public void updateBoardArticle(BoardDto boardDto) {
		log.debug("BoardService - updateBoardArticle 실행");

		BoardEntity boardEntity = boardRepository.findBySeq(boardDto.getBoardSeq())
			.orElseThrow(() -> new IllegalArgumentException("일치하는 게시글 정보가 없습니다."));

		PresetEntity presetEntity = presetRepository.findById(boardDto.getPresetId())
			.orElseThrow(() -> new IllegalArgumentException("일치하는 프리셋 정보가 없습니다."));

		boardEntity.updateBoardArticle(boardDto.getTitle(), boardDto.getContent(), boardDto.getPresetId(),
			presetEntity.getPresetImgSearchName());
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
