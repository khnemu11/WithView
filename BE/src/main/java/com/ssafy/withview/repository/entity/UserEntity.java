package com.ssafy.withview.repository.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.ssafy.withview.repository.dto.UserDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "user")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq;
	private String id;
	private String email;
	private String nickname;
	private String realName;
	private String telephone;
	private String address;
	private String profileImgSearchName;
	private String profileImgOriginalName;

	@CreatedDate
	private LocalDate createTime;
	private LocalDate deleteTime;

	@OneToMany(mappedBy = "serverEntity")
	private List<UserServerEntity> servers = new ArrayList<>();

	@Builder
	public UserEntity(String id, String nickname, String realName, String telephone, String address, String email,
		String profileImgSearchName, String profileImgOriginalName, LocalDate createTime, LocalDate deleteTime) {
		this.id = id;
		this.nickname = nickname;
		this.realName = realName;
		this.telephone = telephone;
		this.address = address;
		this.email = email;
		this.profileImgSearchName = profileImgSearchName;
		this.profileImgOriginalName = profileImgOriginalName;
		this.createTime = createTime;
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
			.realName(userEntity.getRealName())
			.telephone(userEntity.getTelephone())
			.address(userEntity.getAddress())
			.email(userEntity.getEmail())
			.profileImgSearchName(userEntity.getProfileImgSearchName())
			.profileImgOriginalName(userEntity.getProfileImgOriginalName())
			.createTime(userEntity.getCreateTime())
			.deleteTime(userEntity.getDeleteTime())
			.build();
	}
}