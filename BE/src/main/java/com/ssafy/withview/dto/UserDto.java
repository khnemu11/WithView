package com.ssafy.withview.dto;

import java.time.LocalDate;

import com.ssafy.withview.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
	private Long seq;
	private String id;
	private String nickname;
	private String realName;
	private String telephone;
	private String address;
	private String email;
	private String profileImgSearchName;
	private String profileImgOriginalName;
	private String profileMsg;
	private LocalDate createTime;
	private LocalDate deleteTime;

	public static UserEntity toEntity(UserDto userDto) {
		if (userDto == null) {
			return null;
		}
		return UserEntity.builder()
			.id(userDto.getId())
			.nickname(userDto.getNickname())
			.realName(userDto.getRealName())
			.telephone(userDto.getTelephone())
			.address(userDto.getAddress())
			.email(userDto.getEmail())
			.profileImgSearchName(userDto.getProfileImgSearchName())
			.profileImgOriginalName(userDto.getProfileImgOriginalName())
			.profileMsg(userDto.getProfileMsg())
			.deleteTime(userDto.getDeleteTime())
			.build();
	}
}