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
import com.ssafy.withview.dto.ChannelDto;
import com.ssafy.withview.dto.StickerDto;
import com.ssafy.withview.entity.ChannelEntity;
import com.ssafy.withview.entity.ServerEntity;
import com.ssafy.withview.entity.StickerEntity;
import com.ssafy.withview.repository.StickerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StickerServiceImpl implements StickerService{
	private final StickerRepository stickerRepository;
	private final AmazonS3 s3client;

	@Value(value="${cloud.aws.s3.bucket}")
	private String bucketName;

	@Value(value="${DEFAULT_IMG}")
	private String DEFAULT_IMG;

	private String IMG_PREFIX = "sticker/";

	@Override
	@Transactional
	public StickerDto insertSticker(StickerDto stickerDto, MultipartFile file) throws Exception {
		StickerDto result;
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
			stickerDto.setOriginalName(originalName);

			// #3 - 저장용 랜점 파일 이름 저장
			backgroundImgSearchName = uuid.toString()+extend;

			// #4 - 파일 임시 저장
			//파일이 있으면 임시 파일 저장
			backgroundImgFile = File.createTempFile(uuid.toString(),extend);
			FileUtils.copyInputStreamToFile(file.getInputStream(),backgroundImgFile);
			//5 - 이미지 서버 저장
			s3client.putObject(bucketName, IMG_PREFIX+backgroundImgSearchName, backgroundImgFile);
			// #6 - DB 저장
			stickerDto.setSearchName(uuid.toString()+extend);
			backgroundImgFile.delete();
			log.info(stickerDto.toString());

			result = StickerEntity.toDto(stickerRepository.save(StickerDto.toEntity(stickerDto)));
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("채널 생성 중 오류가 발생했습니다.");
		}
		return result;
	}

	@Override
	@Transactional
	public void deleteSticker(Long seq) throws Exception{
		StickerEntity stickerEntity = stickerRepository.findBySeq(seq);

		if(stickerEntity == null){
			throw new Exception("해당 스티커가 없습니다.");
		}

		stickerRepository.delete(stickerEntity);
		s3client.deleteObject(bucketName, "sticker/"+stickerEntity.getSearchName());
	}

	@Override
	public List<StickerDto> findAllStickers() {
		List<StickerEntity> stickerEntityList = stickerRepository.findAll();
		log.info("개수 : ",stickerEntityList.size());
		List<StickerDto> stickerDtoList = new ArrayList<>();

		for(int i=0;i<stickerEntityList.size();i++){
			stickerDtoList.add(StickerEntity.toDto(stickerEntityList.get(i)));
		}

		return stickerDtoList;
	}

	@Override
	public List<StickerDto> findAllStickersByName(String keyword) {
		List<StickerEntity> stickerEntityList = stickerRepository.findAllByOriginalNameContains(keyword);

		List<StickerDto> stickerDtoList = new ArrayList<>();

		for(int i=0;i<stickerEntityList.size();i++){
			stickerDtoList.add(StickerEntity.toDto(stickerEntityList.get(i)));
		}

		return stickerDtoList;
	}

	@Override
	public List<StickerDto> findAllStickersByUserSeq(Long seq) {
		List<StickerEntity> stickerEntityList = stickerRepository.findAllByUserSeq(seq);

		List<StickerDto> stickerDtoList = new ArrayList<>();

		for(int i=0;i<stickerEntityList.size();i++){
			stickerDtoList.add(StickerEntity.toDto(stickerEntityList.get(i)));
		}

		return stickerDtoList;
	}
}
