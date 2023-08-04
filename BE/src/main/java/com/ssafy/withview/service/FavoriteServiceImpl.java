package com.ssafy.withview.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.withview.dto.ServerDto;
import com.ssafy.withview.entity.FavoriteEntity;
import com.ssafy.withview.entity.ServerEntity;
import com.ssafy.withview.entity.UserEntity;
import com.ssafy.withview.repository.FavoriteRepository;
import com.ssafy.withview.repository.ServerRepository;
import com.ssafy.withview.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {
	private final FavoriteRepository favoriteRepository;
	private final UserRepository userRepository;
	private final ServerRepository serverRepository;

	@Override
	public List<ServerDto> findAllFavoriteByUserSeq(Long userSeq) {
		UserEntity userEntity = userRepository.findBySeq(userSeq)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));
		List<FavoriteEntity> favoriteEntityList = favoriteRepository.findAllByUserEntity(userEntity);
		List<ServerDto> serverDtoList = new ArrayList<>();

		for (int i = 0; i < favoriteEntityList.size(); i++) {
			serverDtoList.add(ServerEntity.toDto(favoriteEntityList.get(i).getServerEntity()));
		}

		return serverDtoList;
	}

	@Override
	public void insertFavoriteByUserSeq(Long userSeq, Long serverSeq) throws Exception {
		ServerEntity serverEntity = serverRepository.findBySeq(serverSeq);

		if (serverEntity == null) {
			throw new Exception("해당 서버 없음");
		}

		UserEntity userEntity = userRepository.findBySeq(userSeq)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		if (userEntity == null) {
			throw new Exception("해당 유저 없음");
		}

		FavoriteEntity favoriteEntity = favoriteRepository.findAllByUserEntityAndServerEntity(userEntity, serverEntity);

		if (favoriteEntity != null) {
			throw new Exception("이미 즐겨찾기에 등록됨");
		}

		favoriteEntity = FavoriteEntity.builder()
			.serverEntity(serverEntity)
			.userEntity(userEntity)
			.build();

		favoriteRepository.save(favoriteEntity);
	}

	@Override
	public void deleteFavoriteByUserSeq(Long userSeq, Long serverSeq) throws Exception {
		System.out.println("즐겨찾기 삭제 시작");
		log.info("즐겨찾기 삭제 서비스 시작");

		ServerEntity serverEntity = serverRepository.findBySeq(serverSeq);

		if (serverEntity == null) {
			throw new Exception("해당 서버 없음");
		}

		UserEntity userEntity = userRepository.findBySeq(userSeq)
			.orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

		if (userEntity == null) {
			throw new Exception("해당 유저 없음");
		}

		log.info("user Seq : " + userSeq);
		log.info("user Seq : " + serverSeq);

		FavoriteEntity favoriteEntity = favoriteRepository.findAllByUserEntityAndServerEntity(userEntity, serverEntity);

		favoriteRepository.delete(favoriteEntity);
		log.info("즐겨찾기 삭제 서비스 끝");
	}
}
