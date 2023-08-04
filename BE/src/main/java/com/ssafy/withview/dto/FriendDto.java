package com.ssafy.withview.dto;

import java.time.LocalDateTime;

import com.ssafy.withview.entity.FriendEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendDto {

	private Long followingUserSeq;
	private Long followedUserSeq;
	private LocalDateTime createdTime;

	public static FriendEntity toEntity(FriendDto friendDto) {
		if (friendDto == null) {
			return null;
		}
		return FriendEntity.builder()
			.followedUserSeq(friendDto.getFollowedUserSeq())
			.followingUserSeq(friendDto.getFollowingUserSeq())
			.build();

	}
}
