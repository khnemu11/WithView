package com.ssafy.withview.service;

import java.util.List;

import com.ssafy.withview.dto.ServerDto;

public interface FavoriteService {
	public List<ServerDto> findAllFavoriteByUserSeq(Long userSeq);
	public void insertFavoriteByUserSeq(Long userSeq,Long serverSeq) throws Exception;
	public void deleteFavoriteByUserSeq(Long userSeq,Long serverSeq) throws Exception;
}
