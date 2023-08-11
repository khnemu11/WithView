package com.ssafy.withview.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ssafy.withview.dto.FriendsChatRoomsSeqDto;
import com.ssafy.withview.entity.FriendsChatRoomEntity;
import com.ssafy.withview.entity.FriendsChatRoomUserInfoEntity;
import com.ssafy.withview.repository.FriendsChatRoomRepository;
import com.ssafy.withview.repository.FriendsChatRoomUserInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendsChatRoomService {

	private final FriendsChatRoomRepository friendsChatRoomRepository;
	private final FriendsChatRoomUserInfoRepository friendsChatRoomUserInfoRepository;

	@Transactional
	public Long insertFriendsChatRoom(Long mySeq, Long yourSeq) {
		FriendsChatRoomEntity friendsChatRoom = friendsChatRoomRepository.save(new FriendsChatRoomEntity());
		friendsChatRoomUserInfoRepository.save(FriendsChatRoomUserInfoEntity.builder()
			.friendsChatRoomEntity(friendsChatRoom)
			.userSeq(mySeq)
			.build());
		friendsChatRoomUserInfoRepository.save(FriendsChatRoomUserInfoEntity.builder()
			.friendsChatRoomEntity(friendsChatRoom)
			.userSeq(yourSeq)
			.build());
		return friendsChatRoom.getSeq();
	}

	public List<FriendsChatRoomsSeqDto> findFriendsChatRoomsByPartnerSeq(Long userSeq) {
		Set<FriendsChatRoomUserInfoEntity> chatRoomsByMyUserSeq = friendsChatRoomUserInfoRepository.findAllByUserSeq(
			userSeq);
		Set<FriendsChatRoomUserInfoEntity> chatRoomsByPartnerSeq = chatRoomsByMyUserSeq.stream()
			.map(entity -> friendsChatRoomUserInfoRepository.findBySeqAndUserSeqNot(entity.getSeq(), userSeq))
			.collect(Collectors.toSet());

		return chatRoomsByPartnerSeq.stream()
			.map(entity -> {
				Long seq = entity.getFriendsChatRoomEntity().getSeq();
				Long partnerSeq = entity.getUserSeq();
				return FriendsChatRoomsSeqDto.builder()
					.chatRoomSeq(seq)
					.partnerSeq(partnerSeq)
					.userSeq(userSeq)
					.build();
			})
			.collect(Collectors.toList());
	}


	// public Long findLastReadChatMessageFromChatRoom(Long userSeq, Long friendsChatRoomSeq) {
	//
	// }
}
