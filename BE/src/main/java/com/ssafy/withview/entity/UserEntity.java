package com.ssafy.withview.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ssafy.withview.dto.UserDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity(name = "user")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;
	private String id;
	private String email;
	private String nickname;
	private String profileImgSearchName;
	private String profileImgOriginalName;
	private String profileMsg;

	@CreatedDate
	private LocalDate createTime;
	private LocalDate deleteTime;

	@OneToMany(mappedBy = "userEntity")
	private List<UserServerEntity> userServerEntityList = new ArrayList<>();

	@OneToMany(mappedBy = "userEntity")
	private List<FavoriteEntity> favoriteEntityList = new ArrayList<>();

	@Builder
	public UserEntity(String id, String nickname, String email,
		String profileImgSearchName, String profileImgOriginalName, String profileMsg,
		LocalDate deleteTime) {
		this.id = id;
		this.nickname = nickname;
		this.email = email;
		this.profileImgSearchName = profileImgSearchName;
		this.profileImgOriginalName = profileImgOriginalName;
		this.profileMsg = profileMsg;
		this.deleteTime = deleteTime;
	}

	public static UserDto toDto(UserEntity userEntity) {
		if (userEntity == null) {
			return null;
		}
		return UserDto.builder()
			.seq(userEntity.getSeq())
			.id(userEntity.getId())
			.nickname(userEntity.getNickname())
			.email(userEntity.getEmail())
			.profileImgSearchName(userEntity.getProfileImgSearchName())
			.profileImgOriginalName(userEntity.getProfileImgOriginalName())
			.profileMsg(userEntity.getProfileMsg())
			.createTime(userEntity.getCreateTime())
			.deleteTime(userEntity.getDeleteTime())
			.build();
	}

	public void updateProfileImg(String profileImgOriginalName, String profileImgSearchName) {
		this.profileImgOriginalName = profileImgOriginalName;
		this.profileImgSearchName = profileImgSearchName;
	}

	public void updateProfileMsg(String profileMsg) {
		this.profileMsg = profileMsg;
	}

	public void updateNickName(String nickname) {
		this.nickname = nickname;
	}
}