package com.ssafy.withview.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "friend")
@EntityListeners(AuditingEntityListener.class)
public class FriendEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;
	private Long followingUserSeq;
	private Long followedUserSeq;

	@CreationTimestamp
	private LocalDateTime createdTime;

	@Builder
	public FriendEntity(Long followingUserSeq, Long followedUserSeq) {
		this.followingUserSeq = followingUserSeq;
		this.followedUserSeq = followedUserSeq;
	}
}
