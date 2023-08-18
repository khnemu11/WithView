package com.ssafy.withview.service;

import com.amazonaws.services.s3.AmazonS3;
import com.ssafy.withview.repository.ChannelRepository;
import com.ssafy.withview.repository.ServerRepository;
import com.ssafy.withview.dto.ChannelDto;
import com.ssafy.withview.entity.ChannelEntity;
import com.ssafy.withview.entity.ServerEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
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
@Slf4j
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
	public List<ChannelDto> findAllChannelsByServerSeq(Long serverSeq){
		ServerEntity serverEntity = serverRepository.findBySeq(serverSeq);
		List<ChannelEntity> channelEntities = channelRepository.findAllChannelByServerEntity(serverEntity);
		List<ChannelDto> channelDtos = new ArrayList<>();

		for(ChannelEntity channelEntity : channelEntities){
			channelDtos.add(ChannelEntity.toDto(channelEntity));
		}
		return channelDtos;
	}

	@Override
	public ChannelDto findChannelByChannelSeq(Long channelSeq) {
		ChannelEntity channelEntity= channelRepository.findBySeq(channelSeq);
		ChannelDto channelDto = ChannelEntity.toDto(channelEntity);

		return channelDto;
	}

	@Transactional
	@Override
	public ChannelDto insertChannel(ChannelDto channelDto, MultipartFile multipartFile, Long serverSeq) throws Exception {
		ChannelDto result;
		try{
			if(multipartFile !=null){
				if (!s3client.doesBucketExist(bucketName)) {
					s3client.createBucket(bucketName);
				}
				String originalName = "";
				File backgroundImgFile;
				String backgroundImgSearchName="";
				UUID uuid = UUID.randomUUID();
				String extend = "";

				originalName = multipartFile.getOriginalFilename();
				extend = originalName.substring(originalName.lastIndexOf('.'));
				// #2 - 원본 파일 이름 저장
				channelDto.setBackgroundImgOriginalName(originalName);

				// #3 - 저장용 랜점 파일 이름 저장
				backgroundImgSearchName = uuid.toString()+extend;

				// #4 - 파일 임시 저장
				//파일이 있으면 임시 파일 저장
				backgroundImgFile = File.createTempFile(uuid.toString(),extend);
				FileUtils.copyInputStreamToFile(multipartFile.getInputStream(),backgroundImgFile);
				//5 - 이미지 서버 저장
				s3client.putObject(bucketName, IMG_PREFIX+backgroundImgSearchName, backgroundImgFile);
				// #6 - DB 저장
				channelDto.setBackgroundImgSearchName(uuid.toString()+extend);
				backgroundImgFile.delete();
				log.info(channelDto.toString());
			}

			ServerEntity serverEntity= serverRepository.findBySeq(serverSeq);

			ChannelEntity channelEntity = ChannelEntity.builder()
					.name(channelDto.getName())
					.serverEntity(serverEntity)
					.backgroundImgSearchName(channelDto.getBackgroundImgSearchName())
					.backgroundImgOriginalName(channelDto.getBackgroundImgOriginalName())
					.limitPeople(channelDto.getLimitPeople())
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
	public ChannelDto updateChannel(ChannelDto channelDto,MultipartFile multipartFile,Long serverSeq) throws Exception{
		ChannelEntity channelEntity = channelRepository.findBySeq(channelDto.getSeq());
		channelDto.setBackgroundImgOriginalName(channelEntity.getBackgroundImgOriginalName());
		channelDto.setBackgroundImgSearchName(channelEntity.getBackgroundImgSearchName());

		log.info("대상 채널 " + channelEntity);
		if(channelEntity == null){
			throw new Exception("대상 서버가 없음");
		}
		if(multipartFile != null){
			log.info("=== 파일 변경 ===");
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
			File backgroundImgFile = File.createTempFile(uuid.toString(),extend);
			FileUtils.copyInputStreamToFile(multipartFile.getInputStream(),backgroundImgFile);

			// #5 - 이미지 서버 저장
			s3client.putObject(bucketName, IMG_PREFIX+backgroundImgSearchName, backgroundImgFile);

			// #6 - DB 저장
			channelDto.setBackgroundImgSearchName(uuid.toString()+extend);
			Boolean delete = backgroundImgFile.delete();	//기존 임시 저장용 파일 삭제
		}

		channelEntity.update(channelDto);
		return ChannelEntity.toDto(channelEntity);
	}

	@Transactional
	@Override
	public void deleteChannel(Long channelSeq) throws Exception{
		ChannelEntity channelEntity = channelRepository.findBySeq(channelSeq);

		if(channelEntity == null){
			throw new Exception("해당 채널이 없습니다.");
		}

		channelRepository.delete(channelEntity);
		//S3에 있는 이미지 삭제
		s3client.deleteObject(bucketName, "channel-background/"+channelEntity.getBackgroundImgSearchName());
	}
}
