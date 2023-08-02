package com.ssafy.withview.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.ssafy.withview.repository.FavoriteRepository;
import com.ssafy.withview.repository.ServerRepository;
import com.ssafy.withview.repository.UserRepository;
import com.ssafy.withview.repository.dto.FavoriteDto;
import com.ssafy.withview.repository.dto.ServerDto;
import com.ssafy.withview.repository.entity.FavoriteEntity;
import com.ssafy.withview.repository.entity.ServerEntity;
import com.ssafy.withview.repository.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class FavoriteServiceImpl implements  FavoriteService{
	private final FavoriteRepository favoriteRepository;
	private final UserRepository userRepository;
	private final ServerRepository serverRepository;

	@Override
	public List<ServerDto> findAllFavoriteByUserSeq(Long userSeq) {
		UserEntity userEntity = userRepository.findBySeq(userSeq);
		List<FavoriteEntity>favoriteEntityList = favoriteRepository.findAllByUserEntity(userEntity);
		List<ServerDto> serverDtoList = new ArrayList<>();

		for(int i=0;i<favoriteEntityList.size();i++){
			serverDtoList.add(ServerEntity.toDto(favoriteEntityList.get(i).getServerEntity()));
		}

		return serverDtoList;
	}

	@Override
	public void insertFavoriteByUserSeq(Long userSeq, Long serverSeq) throws Exception{
		ServerEntity serverEntity = serverRepository.findBySeq(serverSeq);

		if(serverEntity ==null){
			throw new Exception("해당 서버 없음");
		}

		UserEntity userEntity = userRepository.findBySeq(userSeq);

		if(userEntity==null){
			throw new Exception("해당 유저 없음");
		}

		FavoriteEntity favoriteEntity = FavoriteEntity.builder()
									.serverEntity(serverEntity)
									.userEntity(userEntity)
									.build();

		favoriteRepository.save(favoriteEntity);
	}

	@Override
	public void deleteFavoriteByUserSeq(Long userSeq, Long serverSeq) throws Exception {
		ServerEntity serverEntity = serverRepository.findBySeq(serverSeq);

		if(serverEntity ==null){
			throw new Exception("해당 서버 없음");
		}

		UserEntity userEntity = userRepository.findBySeq(userSeq);

		if(userEntity==null){
			throw new Exception("해당 유저 없음");
		}

		FavoriteEntity favoriteEntity = FavoriteEntity.builder()
			.serverEntity(serverEntity)
			.userEntity(userEntity)
			.build();

		favoriteRepository.delete(favoriteEntity);
	}
}
