package com.ssafy.withview.entity;

import javax.persistence.*;

import com.ssafy.withview.dto.ServerDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "server")
public class ServerEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;
	private String name;
	@ColumnDefault("5")
	private Integer limitChannel;
	private Long hostSeq;
	private String backgroundImgSearchName;
	private String backgroundImgOriginalName;

	@OneToMany(mappedBy="serverEntity", cascade = CascadeType.REMOVE)
	private List<UserServerEntity> userServerEntityList = new ArrayList<>();

	@OneToMany(mappedBy="serverEntity", cascade = CascadeType.REMOVE)
	private List<ChannelEntity> channelEntityList = new ArrayList<>();

	@OneToMany(mappedBy="serverEntity",cascade = CascadeType.REMOVE)
	private List<FavoriteEntity> favoriteEntityList = new ArrayList<>();


	@Builder
	public ServerEntity(String name, Integer limitChannel, Long hostSeq, String backgroundImgSearchName, String backgroundImgOriginalName) {
		this.name = name;
		this.limitChannel = limitChannel;
		this.hostSeq = hostSeq;
		this.backgroundImgSearchName = backgroundImgSearchName;
		this.backgroundImgOriginalName = backgroundImgOriginalName;
	}

	public static ServerDto toDto(ServerEntity serverEntity){
		if(serverEntity == null){
			return null;
		}
		return ServerDto.builder()
				.seq(serverEntity.getSeq())
			.name(serverEntity.getName())
			.limitChannel(serverEntity.getLimitChannel())
			.hostSeq(serverEntity.getHostSeq())
			.backgroundImgSearchName(serverEntity.getBackgroundImgSearchName())
			.backgroundImgOriginalName(serverEntity.getBackgroundImgOriginalName())
			.build();
	}

	public void update(ServerDto serverDto){
		this.backgroundImgSearchName = serverDto.getBackgroundImgSearchName();
		this.backgroundImgOriginalName = serverDto.getBackgroundImgOriginalName();
		this.name = serverDto.getName();
	}
}