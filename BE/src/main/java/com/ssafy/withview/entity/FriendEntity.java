package com.ssafy.withview.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ssafy.withview.dto.FriendDto;
import com.ssafy.withview.dto.ServerDto;
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
	public FriendEntity(Long followingUserSeq, Long followedUserSeq, LocalDateTime createdTime) {
		this.followingUserSeq = followingUserSeq;
		this.followedUserSeq = followedUserSeq;
		this.createdTime = createdTime;
	}

	public static FriendDto toDto(FriendEntity friendEntity) {
		if (friendEntity == null) {
			return null;
		}
		return FriendDto.builder()
				.followedUserSeq(friendEntity.getFollowedUserSeq())
				.followingUserSeq(friendEntity.getFollowingUserSeq())
				.createdTime(friendEntity.getCreatedTime())
				.build();
	}

}
