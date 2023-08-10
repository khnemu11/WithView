package com.ssafy.withview.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "friends_chat_room_user_info")
@NoArgsConstructor
public class FriendsChatRoomUserInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "friends_chat_room_seq")
	private FriendsChatRoomEntity friendsChatRoomEntity;

	private Long userSeq;
	private Long lastReadMessageSeq;

	@Builder
	public FriendsChatRoomUserInfoEntity(FriendsChatRoomEntity friendsChatRoomEntity, Long userSeq,
		Long lastReadMessageSeq) {
		this.friendsChatRoomEntity = friendsChatRoomEntity;
		this.userSeq = userSeq;
		this.lastReadMessageSeq = lastReadMessageSeq;
	}
}
