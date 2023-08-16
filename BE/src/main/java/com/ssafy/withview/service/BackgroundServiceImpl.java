package com.ssafy.withview.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.ssafy.withview.dto.BackgroundDto;
import com.ssafy.withview.dto.StickerDto;
import com.ssafy.withview.entity.BackgroundEntity;
import com.ssafy.withview.entity.StickerEntity;
import com.ssafy.withview.repository.BackgroundRepository;
import com.ssafy.withview.repository.StickerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackgroundServiceImpl implements BackgroundService{
	private final BackgroundRepository backgroundRepository;
	private final AmazonS3 s3client;

	@Value(value="${cloud.aws.s3.bucket}")
	private String bucketName;

	@Value(value="${DEFAULT_IMG}")
	private String DEFAULT_IMG;

	private String IMG_PREFIX = "channel-background/";


	@Override
	public BackgroundDto insertBackground(BackgroundDto backgroundDto, MultipartFile file) throws Exception {
		BackgroundDto result;
		try{
			if(file == null){
				throw new Exception("파일이 없습니다!");
			}
			if (!s3client.doesBucketExist(bucketName)) {
				s3client.createBucket(bucketName);
			}

			String originalName = "";
			File backgroundImgFile;
			String backgroundImgSearchName="";
			UUID uuid = UUID.randomUUID();
			String extend = "";

			originalName = file.getOriginalFilename();
			extend = originalName.substring(originalName.lastIndexOf('.'));
			// #2 - 원본 파일 이름 저장
			backgroundDto.setOriginalName(originalName);

			// #3 - 저장용 랜점 파일 이름 저장
			backgroundImgSearchName = uuid.toString()+extend;

			// #4 - 파일 임시 저장
			//파일이 있으면 임시 파일 저장
			backgroundImgFile = File.createTempFile(uuid.toString(),extend);
			FileUtils.copyInputStreamToFile(file.getInputStream(),backgroundImgFile);
			//5 - 이미지 서버 저장
			s3client.putObject(bucketName, IMG_PREFIX+backgroundImgSearchName, backgroundImgFile);
			// #6 - DB 저장
			backgroundDto.setSearchName(uuid.toString()+extend);
			backgroundImgFile.delete();
			log.info(backgroundDto.toString());

			result = BackgroundEntity.toDto(backgroundRepository.save(BackgroundDto.toEntity(backgroundDto)));
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("채널 생성 중 오류가 발생했습니다.");
		}
		return result;
	}

	@Override
	public void deleteBackground(Long seq) throws Exception {
		BackgroundEntity stickerEntity = backgroundRepository.findBySeq(seq);

		if(stickerEntity == null){
			throw new Exception("해당 스티커가 없습니다.");
		}

		backgroundRepository.delete(stickerEntity);
		s3client.deleteObject(bucketName, IMG_PREFIX+stickerEntity.getSearchName());
	}

	@Override
	public List<BackgroundDto> findAllBackgrounds() {
		List<BackgroundEntity> backgroundEntityList = backgroundRepository.findAll();
		log.info("개수 : ",backgroundEntityList.size());
		List<BackgroundDto> backgroundDtoList = new ArrayList<>();

		for(int i=0;i<backgroundEntityList.size();i++){
			backgroundDtoList.add(BackgroundEntity.toDto(backgroundEntityList.get(i)));
		}

		return backgroundDtoList;
	}

	@Override
	public List<BackgroundDto> findAllBackgroundsByName(String keyword) {
		List<BackgroundEntity> backgroundEntityList = backgroundRepository.findAllByOriginalNameContains(keyword);

		List<BackgroundDto> backgroundDtoList = new ArrayList<>();

		for(int i=0;i<backgroundEntityList.size();i++){
			backgroundDtoList.add(BackgroundEntity.toDto(backgroundEntityList.get(i)));
		}

		return backgroundDtoList;
	}
	@Override
	public List<BackgroundDto> findAllBackgroundsByUserSeq(BackgroundDto backgroundDto) {
		List<BackgroundEntity> backgroundEntityList;


		if(backgroundDto.getKeyword() == null || backgroundDto.getKeyword().length() == 0){
			log.info("유저의 모든 스티커 검색");
			backgroundEntityList = backgroundRepository.findAllByUserSeq(backgroundDto.getUserSeq());
		}else{
			log.info("유저의 특정 스티커 검색");
			backgroundEntityList = backgroundRepository.findAllByUserSeqAndOriginalNameContains(backgroundDto.getUserSeq(),backgroundDto.getKeyword());

		}

		List<BackgroundDto> backgroundDtoList = new ArrayList<>();

		for(int i=0;i<backgroundEntityList.size();i++){
			backgroundDtoList.add(BackgroundEntity.toDto(backgroundEntityList.get(i)));
		}

		return backgroundDtoList;
	}
}
