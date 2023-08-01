package com.ssafy.withview.service;

import com.amazonaws.services.s3.AmazonS3;
import com.ssafy.withview.repository.ChannelRepository;
import com.ssafy.withview.repository.ServerRepository;
import com.ssafy.withview.repository.dto.ChannelDto;
import com.ssafy.withview.repository.dto.ServerDto;
import com.ssafy.withview.repository.entity.ChannelEntity;
import com.ssafy.withview.repository.entity.ServerEntity;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService{
	private final ServerRepository serverRepository;
	private final ChannelRepository channelRepository;
	private final ResourceLoader resourceLoader;
	private final AmazonS3 s3client;

	@Value(value="${cloud.aws.s3.bucket}")
	private String bucketName;

	@Value(value="${DEFAULT_IMG}")
	private String DEFAULT_IMG;

	private String IMG_PREFIX = "channel-background/";
	@Override
	public List<ChannelDto> findAllChannelsByServerSeq(long serverSeq){
		ServerEntity serverEntity = serverRepository.findBySeq(serverSeq);
		List<ChannelEntity> channelEntities = channelRepository.findAllChannelByServerEntity(serverEntity);
		List<ChannelDto> channelDtos = new ArrayList<>();

		for(ChannelEntity channelEntity : channelEntities){
			channelDtos.add(ChannelEntity.toDto(channelEntity));
		}
		return channelDtos;
	}

	@Override
	public ChannelDto findChannelByChannelSeq(long channelSeq) {
		ChannelEntity channelEntity= channelRepository.findBySeq(channelSeq);
		ChannelDto channelDto = ChannelEntity.toDto(channelEntity);

		return channelDto;
	}

	@Transactional
	@Override
	public ChannelDto insertChannel(ChannelDto channelDto, MultipartFile multipartFile, long serverSeq) throws Exception {
		ChannelDto result;
		try{


			if (!s3client.doesBucketExist(bucketName)) {
				s3client.createBucket(bucketName);
			}
			String originalName = "";
			File backgroundImgFile;
			String backgroundImgSearchName="";
			UUID uuid = UUID.randomUUID();
			String extend = "";
			//사진이 없는경우 로고 사진으로 대체
			if(multipartFile == null){
				originalName=DEFAULT_IMG;
			}
			//사진이 있으면 해당 사진을 배경화면으로
			else{
				originalName = multipartFile.getOriginalFilename();
			}

			extend = originalName.substring(originalName.lastIndexOf('.'));
			// #2 - 원본 파일 이름 저장
			channelDto.setBackgroundImgOriginalName(originalName);

			// #3 - 저장용 랜점 파일 이름 저장
			backgroundImgSearchName = uuid.toString()+extend;

			// #4 - 파일 임시 저장
			//파일이 있으면 임시 파일 저장
			if(multipartFile!=null){
				backgroundImgFile = new File(resourceLoader.getResource("classpath:/img/").getFile().getAbsolutePath(),backgroundImgSearchName);
				multipartFile.transferTo(backgroundImgFile);
			}else{
				backgroundImgFile = new File(resourceLoader.getResource("classpath:/img/").getFile().getAbsolutePath(),originalName);
			}
			// #5 - 이미지 서버 저장
			s3client.putObject(bucketName, IMG_PREFIX+backgroundImgSearchName, backgroundImgFile);
			// #6 - DB 저장
			channelDto.setBackgroundImgSearchName(uuid.toString()+extend);
			System.out.println(channelDto);
			ServerEntity serverEntity= serverRepository.findBySeq(serverSeq);
			ChannelEntity channelEntity = ChannelEntity.builder()
					.name(channelDto.getName())
					.serverEntity(serverEntity)
					.backgroundImgSearchName(channelDto.getBackgroundImgSearchName())
					.backgroundImgOriginalName(channelDto.getBackgroundImgOriginalName())
					.build();

			result = ChannelEntity.toDto(channelRepository.save(channelEntity));
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("채널 생성 중 오류가 발생했습니다.");
		}
		return result;
	}

	@Transactional
	@Override
	public ChannelDto updateChannel(ChannelDto channelDto,MultipartFile multipartFile,long serverSeq) throws Exception{
		ChannelEntity channelEntity = channelRepository.findBySeq(channelDto.getSeq());
		System.out.println("대상 채널 " + channelEntity);
		if(channelEntity == null){
			throw new Exception("대상 서버가 없음");
		}
		if(multipartFile != null){
			System.out.println("=== 파일 변경 ===");
			if (!s3client.doesBucketExist(bucketName)) {
				s3client.createBucket(bucketName);
			}
			// #2 - 원본 파일 이름 저장
			String originalName = multipartFile.getOriginalFilename();
			channelDto.setBackgroundImgOriginalName(originalName);

			// #3 - 저장용 랜덤 파일 이름 저장
			String extend = originalName.substring(originalName.lastIndexOf('.'));
			UUID uuid = UUID.randomUUID();
			String backgroundImgSearchName = uuid.toString()+extend;

			// #4 - 파일 임시 저장
			File backgroundImgFile = new File(resourceLoader.getResource("classpath:/img/").getFile().getAbsolutePath(),backgroundImgSearchName);
			multipartFile.transferTo(backgroundImgFile);

			// #5 - 이미지 서버 저장
			s3client.putObject(bucketName, IMG_PREFIX+backgroundImgSearchName, backgroundImgFile);

			// #6 - DB 저장
			channelDto.setBackgroundImgSearchName(uuid.toString()+extend);
			backgroundImgFile.delete();	//기존 임시 저장용 파일 삭제
		}

		channelEntity.update(channelDto);
		return ChannelEntity.toDto(channelEntity);
	}

	@Transactional
	@Override
	public void deleteChannel(long channelSeq) throws Exception{
		ChannelEntity channelEntity = channelRepository.findBySeq(channelSeq);

		if(channelEntity == null){
			throw new Exception("해당 채널이 없습니다.");
		}

		channelRepository.delete(channelEntity);
		//S3에 있는 이미지 삭제
		s3client.deleteObject(bucketName, "channel-background/"+channelEntity.getBackgroundImgSearchName());
	}
}
