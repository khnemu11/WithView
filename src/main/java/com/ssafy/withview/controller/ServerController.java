package com.ssafy.withview.controller;

import com.amazonaws.services.s3.AmazonS3;
import org.bson.json.JsonObject;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.withview.repository.ServerRepository;
import com.ssafy.withview.repository.dto.ServerDto;
import com.ssafy.withview.repository.entity.ServerEntity;
import com.ssafy.withview.service.ServerService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/api/server")
@RequiredArgsConstructor
public class ServerController {
	private final ServerService serverService;
	private final ResourceLoader resourceLoader;
	private final AmazonS3 s3client;

	@Value(value="${cloud.aws.s3.bucket}")
	private String bucketName;
	@Value(value = "${CLOUD_FRONT_URL}")
	private String CLOUD_FRONT_URL;

	@GetMapping("/test")
	public void test() {
		ServerDto inputServerDto= ServerDto.builder()
			.name("롤")
			.hostSeq(1)
			.build();

		System.out.println(inputServerDto);
	}
	@GetMapping("/{serverSeq}")
	public ResponseEntity<?> findServerBySeq(@PathVariable long serverSeq) {
		JSONObject jsonObject = new JSONObject();
		try{
			ServerDto serverDto = serverService.findServerBySeq(serverSeq);

			jsonObject.put("success",true);
			jsonObject.put("server",serverDto);
		}catch (Exception e){
			e.printStackTrace();
			jsonObject = new JSONObject();
			jsonObject.put("succuess",false);
			jsonObject.put("msg",serverSeq+"서버 찾기를 실패했습니다.");
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	}
	@GetMapping("/find-server-by-name")
	public ResponseEntity<?> findServerBySeq(@RequestParam("name") String name,@RequestParam("userSeq") String userSeq) {
		JSONObject jsonObject = new JSONObject();
		try{

			jsonObject.put("success",true);
//			jsonObject.put("server",serverDto);
		}catch (Exception e){
			e.printStackTrace();
			jsonObject = new JSONObject();
			jsonObject.put("succuess",false);
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	}
	@PostMapping("/")
	public ResponseEntity<?> addServer(@ModelAttribute ServerDto serverDto, @RequestParam("file") MultipartFile multipartFile) {
		JSONObject jsonObject = new JSONObject();
		try{
			// #1 - 버킷 생성
			if (!s3client.doesBucketExist(bucketName)) {
				s3client.createBucket(bucketName);
			}
			// #2 - 원본 파일 이름 저장
			String originalName = multipartFile.getOriginalFilename();
			serverDto.setBackgroundImgOriginalName(originalName);

			// #3 - 저장용 랜점 파일 이름 저장
			String extend = originalName.substring(originalName.lastIndexOf('.'));
			UUID uuid = UUID.randomUUID();
			String backgroundImgSearchName = uuid.toString()+extend;
			
			// #4 - 파일 임시 저장
			File backgroundImgFile = new File(resourceLoader.getResource("classpath:/img/").getFile().getAbsolutePath(),backgroundImgSearchName);
			multipartFile.transferTo(backgroundImgFile);

			// #5 - 이미지 서버 저장
			s3client.putObject(bucketName, "server-background/"+backgroundImgSearchName, backgroundImgFile);
			jsonObject.put("imgUrl",CLOUD_FRONT_URL+"server-background/"+backgroundImgSearchName);
			// #6 - DB 저장
			serverDto.setBackgroundImgSearchName(uuid.toString()+extend);
			serverDto = serverService.insertServer(serverDto);
			backgroundImgFile.delete();	//기존 임시 저장용 파일 삭제
		}catch (Exception e){
			jsonObject.put("success",false);
			jsonObject.put("msg","서버 추가를 실패했습니다.");
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
		}

		jsonObject.put("success",true);
		jsonObject.put("server",serverDto);
		jsonObject.put("msg","서버 추가를 성공했습니다.");

		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	}
}
