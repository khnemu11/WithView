package com.ssafy.withview.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;

@Getter
@Entity
@Table(name = "friends_chat_room")
public class FriendsChatRoomEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;

	@CreationTimestamp
	private LocalDateTime createdTime;

	@OneToMany(mappedBy = "friendsChatRoomEntity")
	private List<FriendsChatRoomUserInfoEntity> friendsChatRoomUserInfoEntities;
}
