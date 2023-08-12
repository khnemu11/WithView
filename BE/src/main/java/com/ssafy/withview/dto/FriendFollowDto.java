package com.ssafy.withview.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendFollowDto {

	private Long followingUserSeq;
	private Long followedUserSeq;
}
