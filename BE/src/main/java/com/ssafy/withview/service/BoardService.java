package com.ssafy.withview.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.withview.dto.BoardDto;
import com.ssafy.withview.entity.BoardEntity;
import com.ssafy.withview.repository.BoardRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

	private final BoardRepository boardRepository;

	/**
	 * 게시글 작성
	 *
	 * @param boardDto (작성자 pk 값, 제목, 내용, 프리셋 이름)
	 */
	@Transactional
	public void writeBoardArticle(BoardDto boardDto) {
		boardRepository.save(BoardEntity.builder()
			.userSeq(boardDto.getUserSeq())
			.title(boardDto.getTitle())
			.content(boardDto.getContent())
			.presetImgSearchName(boardDto.getPresetImgSearchName())
			.build());
	}

	/**
	 * 게시글 목록
	 */
	@Transactional
	public List<BoardDto> getBoardArticles() {
		return boardRepository.findAll().stream()
			.map(b -> BoardDto.builder()
				.seq(b.getSeq())
				.userSeq(b.getUserSeq())
				.title(b.getTitle())
				.content(b.getContent())
				.registerTime(b.getRegisterTime())
				.build())
			.collect(Collectors.toList());
	}
}
