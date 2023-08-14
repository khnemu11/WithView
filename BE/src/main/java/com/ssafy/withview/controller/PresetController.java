package com.ssafy.withview.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.withview.dto.PresetDto;
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
	 *
	 * @param userSeq    (유저 pk 값)
	 * @param presetName (프리셋 이름)
	 * @param stage      (프리셋 stage 정보)
	 * @param file       (프리셋 png 이미지)
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
			log.debug("프리셋 저장 성공. userSeq: {}", userSeq);
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 프리셋 저장 실패, ErrorMessage: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 저장한 프리셋 목록 확인
	 *
	 * @param userSeq (프리셋을 저장한 유저의 pk 값)
	 * @return ResponseEntity (true / false, 상태코드, PresetInfo - 프리셋의 id, 이름, png 이름, 등록일)
	 */
	@GetMapping("/{userSeq}/list")
	public ResponseEntity<Map<String, Object>> getPresetList(@PathVariable(value = "userSeq") Long userSeq) {
		log.debug("PresetController - getPresetList 실행: 저장한 프리셋 목록 확인");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			List<PresetDto> presetDtos = presetService.getPresetList(userSeq);
			resultMap.put("PresetListInfo", presetDtos);
			resultMap.put("success", true);
			status = HttpStatus.OK;
			log.debug("프리셋 목록 불러오기 성공, userSeq: {}", userSeq);
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 프리셋 목록 불러오기 실패, ErrorMessage: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}

	/**
	 * 선택한 프리셋 가져오기
	 *
	 * @param id (선택한 프리셋의 pk 값)
	 * @return ResponseEntity (true / false, 상태코드, Stage)
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getPreset(@PathVariable(value = "id") String id) {
		log.debug("PresetController - getPreset 실행: 선택한 프리셋 가져오기");
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status;
		try {
			PresetDto presetDto = presetService.getPreset(id);
			resultMap.put("PresetInfo", presetDto);
			resultMap.put("success", true);
			status = HttpStatus.OK;
			log.debug("선택한 프리셋 가져오기 성공, id: {}", id);
		} catch (IllegalArgumentException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.NOT_FOUND;
			log.info("[Error] 선택한 프리셋 가져오기 실패, ErrorMessage: {}", e.getMessage());
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("[Error] 선택한 프리셋 가져오기 실패, ErrorMessage: {}", e.getMessage());
		}
		return new ResponseEntity<>(resultMap, status);
	}
}
