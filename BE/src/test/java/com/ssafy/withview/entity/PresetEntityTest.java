package com.ssafy.withview.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ssafy.withview.dto.PresetDto;

class PresetEntityTest {

	@Test
	@DisplayName("PresetEntity 생성 확인 테스트")
	public void createPresetEntity() {
		// given
		PresetEntity presetEntity = PresetEntity.builder()
			.userSeq(1l)
			.presetName("testPresetName")
			.presetImgSearchName("testPresetImgSearchName")
			.stage("testStage")
			.build();

		// when, then
		Assertions.assertThat(presetEntity.getUserSeq()).isEqualTo(1l);
		Assertions.assertThat(presetEntity.getPresetName()).isEqualTo("testPresetName");
		Assertions.assertThat(presetEntity.getPresetImgSearchName()).isEqualTo("testPresetImgSearchName");
		Assertions.assertThat(presetEntity.getStage()).isEqualTo("testStage");
	}

	@Test
	@DisplayName("PresetEntity -> PresetDto 변환 테스트")
	void toDto() {
		PresetEntity presetEntity = PresetEntity.builder()
			.userSeq(1l)
			.presetName("testPresetName")
			.presetImgSearchName("testPresetImgSearchName")
			.stage("testStage")
			.build();

		PresetDto presetDto = PresetEntity.toDto(presetEntity);
		Assertions.assertThat(presetDto.getId()).isEqualTo(presetEntity.getId());
		Assertions.assertThat(presetDto.getUserSeq()).isEqualTo(presetEntity.getUserSeq());
		Assertions.assertThat(presetDto.getPresetName()).isEqualTo(presetEntity.getPresetName());
		Assertions.assertThat(presetDto.getPresetImgSearchName()).isEqualTo(presetEntity.getPresetImgSearchName());
		Assertions.assertThat(presetDto.getStage()).isEqualTo(presetEntity.getStage());
		Assertions.assertThat(presetDto.getRegisterTime()).isEqualTo(presetEntity.getRegisterTime());
	}
}