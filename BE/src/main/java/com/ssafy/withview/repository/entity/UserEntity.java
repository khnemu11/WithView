package com.ssafy.withview.repository.entity;

import com.ssafy.withview.repository.dto.ServerDto;
import com.ssafy.withview.repository.dto.UserDto;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name="user")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq;
	private String id;
	private String nickname;
	private String realName;
	private String telephone;
	private String address;
	private String email;

	@CreatedDate
	private LocalDate createTime;
	private LocalDate deleteTime;

	@OneToMany(mappedBy="userEntity", cascade = CascadeType.REMOVE)
	private List<UserServerEntity> userServerEntityList = new ArrayList<>();

	@Builder
	public UserEntity(String id, String nickname, String realName, String telephone, String address, String email, LocalDate createTime,LocalDate deleteTime){
		this.id= id;
		this.nickname = nickname;
		this.realName = realName;
		this.telephone = telephone;
		this.address = address;
		this.email = email;
		this.createTime = createTime;
		this.deleteTime = deleteTime;
	}

	public static UserDto toDto(UserEntity userEntity){
		if(userEntity == null){
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
				.createTime(userEntity.getCreateTime())
				.deleteTime(userEntity.getDeleteTime())
				.build();
	}
}