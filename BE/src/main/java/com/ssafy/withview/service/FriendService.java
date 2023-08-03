package com.ssafy.withview.service;

import org.springframework.stereotype.Service;

import com.ssafy.withview.repository.FriendRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FriendService {

	private final FriendRepository friendRepository;

	public void follow(Long followingUserSeq, Long followedUserSe) {

		// friendRepository.save()
	}
}
