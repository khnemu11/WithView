package com.ssafy.withview.controller;

import com.ssafy.withview.dto.ServerDto;
import com.ssafy.withview.dto.UserDto;
import com.ssafy.withview.service.FavoriteService;
import com.ssafy.withview.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
@Slf4j
public class ServerController {
	private final ServerService serverService;
	private final FavoriteService favoriteService;

	@Value(value = "${CLOUD_FRONT_URL}")
	private String CLOUD_FRONT_URL;

	@GetMapping("/{serverSeq}")
	public ResponseEntity<?> findServerBySeq(@PathVariable Long serverSeq) {
		log.info("서버 탐색 시작");
		JSONObject result = new JSONObject();
		try{
			ServerDto serverDto = serverService.findServerBySeq(serverSeq);
			log.info("찾은 서버 "+ serverDto);
			result.put("success",true);
			result.put("server",serverDto);
			result.put("imgUriPrefix",CLOUD_FRONT_URL+"server-background/");
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("success",false);
			result.put("msg",serverSeq+"서버 찾기를 실패했습니다.");
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("")
	public ResponseEntity<?> findAllServers() {
		log.info("모든 서버 찾기");
		JSONObject result = new JSONObject();
		try{
			List<ServerDto> serverDtoList = serverService.findAllServer();
			result.put("success",true);
			result.put("servers",serverDtoList);
			result.put("imgUriPrefix",CLOUD_FRONT_URL+"server-background/");
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("success",false);
			result.put("msg","서버 찾기를 실패했습니다.");
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@GetMapping("/{serverSeq}/users")
	public ResponseEntity<?> findAllUsersInServer(@PathVariable(name = "serverSeq") Long serverSeq) {
		JSONObject result = new JSONObject();
		try{
			List<UserDto> userDtoList = serverService.findAllUsersByServerSeq(serverSeq);
			result.put("success",true);
			result.put("users",userDtoList);
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("success",false);
			result.put("msg","서버 참가자 목록 불러오기를 실패했습니다.");
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@GetMapping("/find-server-by-user")
	public ResponseEntity<?> findServerByUser(@RequestParam("userSeq") Long userSeq) {
		JSONObject result = new JSONObject();
		try{
			List<ServerDto> serverDtoList = serverService.findAllServerByUserSeq(userSeq);
			List<ServerDto> favoriteDtoList = favoriteService.findAllFavoriteByUserSeq(userSeq);
			Set<Long> favoriteSet = new HashSet<>();

			for(ServerDto favorite : favoriteDtoList){
				favoriteSet.add(favorite.getSeq());
			}

			for(int i=0;i<serverDtoList.size();i++){
				if(!favoriteSet.contains(serverDtoList.get(i).getSeq())){
					serverDtoList.get(i).setIsFavorite(false);
				}else {
					serverDtoList.get(i).setIsFavorite(true);
				}
			}


			result.put("success",true);
			result.put("servers",serverDtoList);
			result.put("imgUriPrefix",CLOUD_FRONT_URL+"server-background/");
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("success",false);
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@PostMapping("")
	public ResponseEntity<?> insertServer(@ModelAttribute ServerDto serverDto,@RequestParam(name = "file",required = false) MultipartFile multipartFile) {
		log.info("====== 서버 추가 시작 ======");
		log.info("====== 입력 서버 정보 ======");
		log.info(serverDto.toString());
		log.info("====== 입력 파일 정보 ======");
		log.info(" "+multipartFile);
		JSONObject result = new JSONObject();
		try{
			serverDto = serverService.insertServer(serverDto,multipartFile);
			log.info("생성한 서버"+serverDto);
			serverService.enterServer(serverDto.getSeq(),serverDto.getHostSeq());
		}catch (Exception e){
			e.printStackTrace();
			result.put("success",false);
			result.put("msg",e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		result.put("success",true);
		result.put("server",serverDto);
		result.put("imgUrl",CLOUD_FRONT_URL+"server-background/"+serverDto.getBackgroundImgSearchName());
		result.put("msg","서버 추가를 성공했습니다.");
		log.info("====== 서버 추가 끝 ======");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@PostMapping("/enter")
	public ResponseEntity<?> enterServer(@RequestParam(name="serverSeq")Long serverSeq,@RequestParam(name="userSeq")Long userSeq) {
		log.info("====== 서버 입장 시작 ======");
		log.info("서버 seq" + serverSeq);
		log.info("유저 seq" + userSeq);
		JSONObject result = new JSONObject();
		try{
			// #1 - 버킷 생성
			serverService.enterServer(serverSeq,userSeq);
		}catch (Exception e){
			e.printStackTrace();
			result.put("success",false);
			result.put("msg",e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.OK);
		}

		result.put("success",true);
		result.put("msg","서버 입장울 성공했습니다.");
		log.info("====== 서버 추가 끝 ======");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@DeleteMapping("/leave")
	public ResponseEntity<?> leaveServer(@RequestParam(name="serverSeq")Long serverSeq,@RequestParam(name="userSeq")Long userSeq) {
		log.info("====== 서버 퇴장 시작 ======");
		JSONObject result = new JSONObject();
		try{
			// #1 - 버킷 생성
			serverService.leaveServer(serverSeq,userSeq);
		}catch (Exception e){
			e.printStackTrace();
			result.put("success",false);
			result.put("msg",e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.OK);
		}

		result.put("success",true);
		result.put("msg","서버 퇴장을 성공했습니다.");
		log.info("====== 서버 퇴장 끝 ======");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@PostMapping("/{serverSeq}")
	public ResponseEntity<?> updateServer(@PathVariable(name = "serverSeq") Long serverSeq,@ModelAttribute ServerDto serverDto, @RequestParam(name = "file", required = false) MultipartFile multipartFile) {
		log.info("====== 서버 변경 시작 ======");
		JSONObject result = new JSONObject();
		try{
			serverDto = serverService.updateServer(serverDto,multipartFile);

			result.put("success",true);
			result.put("server",serverDto);
			result.put("msg","서버 수정을 성공했습니다.");
			result.put("imgUrl",CLOUD_FRONT_URL+"server-background/"+serverDto.getBackgroundImgSearchName());

		}catch (Exception e){
			e.printStackTrace();
			result.put("success",false);
			result.put("msg",e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		log.info("====== 서버 변경 끝 ======");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@DeleteMapping("")
	public ResponseEntity<?> deleteServer(@RequestParam(name="serverSeq") Long serverSeq,
										  @RequestParam(name="userSeq") Long userSeq) {
		log.info("====== 서버 삭제 시작 ======");
		JSONObject result = new JSONObject();	//결과 json 변수
		try{
			serverService.deleteServer(serverSeq,userSeq);
			result.put("success",true);
			result.put("msg","서버 삭제를 성공했습니다.");
		}catch (Exception e){
			e.printStackTrace();
			result.put("success",false);
			result.put("msg",e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.OK);
		}

		log.info("====== 서버 삭제 끝 ======");
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
