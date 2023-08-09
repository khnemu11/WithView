package com.ssafy.withview.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.withview.service.PresetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/preset")
@RequiredArgsConstructor
@Slf4j
public class PresetController {

	private final PresetService presetService;

	/**
	 * 프리셋 저장
	 * @param userSeq (유저 pk 값)
	 * @param presetName (프리셋 이름)
	 * @param stage (프리셋 stage 정보)
	 * @param file (프리셋 png 이미지)
	 * @return ResponseEntity (true / false, 상태코드)
	 */
	@PostMapping("")
	public ResponseEntity<Map<String, Object>> savePreset(
		@RequestParam(value = "userSeq") Long userSeq,
		@RequestParam(value = "presetName") String presetName,
		@RequestParam(value = "stage") String stage,
		@RequestParam(value = "file") MultipartFile file
	) {
		log.debug("PresetController - savePreset 실행: 프리셋 저장");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			presetService.savePreset(userSeq, presetName, stage, file);
			resultMap.put("success", true);
			status = HttpStatus.OK;
			log.debug("프리셋 저장 완료. userSeq: {}", userSeq);
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 프리셋 저장 실패: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}
}
