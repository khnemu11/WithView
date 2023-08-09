package com.ssafy.withview.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.ssafy.withview.entity.PresetEntity;
import com.ssafy.withview.repository.PresetRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresetService {

	private final PresetRepository presetRepository;

	private final AmazonS3 s3client;

	@Value(value = "${cloud.aws.s3.bucket}")
	private String bucketName;

	/**
	 * 프리셋 저장 (정보: Mongo DB 저장, 이미지 파일: Amazon S3 저장)
	 *
	 * @param userSeq (유저 pk 값)
	 * @param presetName (프리셋 이름)
	 * @param stage (프리셋 stage 정보)
	 * @param file (프리셋 png 이미지)
	 */
	@Transactional
	public void savePreset(Long userSeq, String presetName, String stage, MultipartFile file) throws
		IOException {
		log.debug("PresetService - savePreset 실행");

		// Amazon S3 이미지 저장
		if (!s3client.doesBucketExistV2(bucketName)) {
			s3client.createBucket(bucketName);
		}

		String originalName = file.getOriginalFilename();
		UUID uuid = UUID.randomUUID();

		String extend = originalName.substring(originalName.lastIndexOf('.'));
		String searchName = uuid + extend;

		File presetImgFile = File.createTempFile(uuid.toString(), extend);
		FileUtils.copyInputStreamToFile(file.getInputStream(), presetImgFile);

		s3client.putObject(bucketName, "preset/" + searchName, presetImgFile);
		log.debug("Amazon S3 Bucket: 이미지 저장 완료");

		presetRepository.save(
			PresetEntity.builder()
				.userSeq(userSeq)
				.presetName(presetName)
				.presetImgSearchName(searchName)
				.stage(stage)
				.build());
	}

	/**
	 * 프리셋 목록 확인
	 *
	 */

	/**
	 * 프리셋 불러오기
	 *
	 */
}
