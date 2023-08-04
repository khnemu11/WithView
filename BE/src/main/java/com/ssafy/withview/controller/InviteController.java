package com.ssafy.withview.controller;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.withview.dto.ServerDto;
import com.ssafy.withview.service.ServerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/invite")
@RequiredArgsConstructor
@Slf4j
public class InviteController {
	private final ServerService serverService;

	@Value(value = "${FRONT_URL}")
	private String FRONT_URL;

	@GetMapping("/{code}")
	public ResponseEntity<?> findInviteServer(@PathVariable(name = "code") String code,HttpSession session) {
		log.info("===== 서버 초대 링크 접속 시작 =====");
		JSONObject result = new JSONObject();
		try{
			ServerDto serverDto = (ServerDto)session.getAttribute(code);

			if(serverDto == null){
				result.put("success",false);
				result.put("msg","유효하지 않는 초대링크 입니다.");
				return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			result.put("success",true);
			result.put("server",serverDto);
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("success",false);
			result.put("msg",e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@PostMapping("")
	public ResponseEntity<?> insertInviteCode(@RequestParam(name ="severSeq") Long serverSeq,@RequestParam(name ="userSeq") Long userSeq, HttpSession session) {
		log.info("===== 서버 초대 링크 생성 시작 =====");
		JSONObject result = new JSONObject();
		try{
			String inviteCode = serverService.insertInviteCode(serverSeq,userSeq);
			ServerDto serverDto = serverService.findServerBySeq(serverSeq);

			log.info("초대 코드 "+ inviteCode);
			String url = FRONT_URL +"serverenter/"+inviteCode;
			session.setAttribute(inviteCode,serverDto);

			result.put("success",true);
			result.put("link",url);
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put("success",false);
			result.put("msg",e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
