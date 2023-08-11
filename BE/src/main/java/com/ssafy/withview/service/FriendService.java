package com.ssafy.withview.service;

import com.ssafy.withview.dto.FriendDto;
import com.ssafy.withview.entity.ChannelEntity;
import com.ssafy.withview.entity.FriendEntity;
import org.springframework.stereotype.Service;

import com.ssafy.withview.repository.FriendRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FriendService {

	private final FriendRepository friendRepository;

	public List<FriendDto> getFollowingUsers(Long userSeq) {
		return friendRepository.findAllByFollowingUserSeq(userSeq).stream().map(FriendEntity::toDto).collect(Collectors.toList());
	}

	public FriendDto insertFollowUser(Long followingUserSeq, Long followedUserSeq) {
		if (friendRepository.findByFollowingUserSeqAndFollowedUserSeq(followingUserSeq, followedUserSeq).isEmpty()) {
			FriendDto friendDto = FriendDto.builder()
				.followingUserSeq(followingUserSeq)
				.followedUserSeq(followedUserSeq)
				.build();
			FriendEntity save = friendRepository.save(FriendDto.toEntity(friendDto));
			return FriendEntity.toDto(save);
		}
		return null;
	}
}
