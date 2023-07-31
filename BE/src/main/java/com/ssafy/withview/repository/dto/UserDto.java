package com.ssafy.withview.repository.dto;

import com.ssafy.withview.repository.entity.ServerEntity;
import com.ssafy.withview.repository.entity.UserEntity;
import lombok.*;
import org.apache.catalina.User;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	private long seq;
	private String id;
	private String nickname;
	private String realName;
	private String telephone;
	private String address;
	private String email;
	private LocalDate createTime;
	private LocalDate deleteTime;

	public static UserEntity toEntity(UserDto userDto){
		if(userDto == null){
			return null;
		}
		return UserEntity.builder()
				.id(userDto.getId())
				.nickname(userDto.getNickname())
				.realName(userDto.getRealName())
				.telephone(userDto.getTelephone())
				.address(userDto.getAddress())
				.email(userDto.getEmail())
				.createTime(userDto.getCreateTime())
				.deleteTime(userDto.getDeleteTime())
				.build();
	}
}