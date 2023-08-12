package com.ssafy.withview.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ssafy.withview.dto.FavoriteDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "favorite")
public class FavoriteEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="server_seq")
	private ServerEntity serverEntity;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_seq")
	private UserEntity userEntity;

	@Builder
	public FavoriteEntity(ServerEntity serverEntity,UserEntity userEntity) {
		this.userEntity = userEntity;
		this.serverEntity = serverEntity;
	}

	public String toString(){
		return userEntity.getSeq()+" have "+serverEntity.getSeq();
	}
	public static FavoriteDto toDto(FavoriteEntity favoriteEntity){
		if(favoriteEntity == null){
			return null;
		}

		return FavoriteDto.builder()
			.seq(favoriteEntity.getSeq())
			.serverSeq(favoriteEntity.getServerEntity().getSeq())
			.serverDto(ServerEntity.toDto(favoriteEntity.getServerEntity()))
			.userSeq(favoriteEntity.getUserEntity().getSeq())
			.userDto(UserEntity.toDto(favoriteEntity.getUserEntity()))
			.build();
	}
}