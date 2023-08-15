package com.ssafy.withview.controller;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.withview.dto.CanvasDto;
import com.ssafy.withview.dto.CanvasMessageDto;
import com.ssafy.withview.service.CanvasService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CanvasMessagingController {
	private final CanvasService canvasService;

	@MessageMapping("/canvas/channel/{channelSeq}")
	public void sendCanvas(@DestinationVariable(value = "channelSeq")Long channelSeq, @Payload Map<String,String> parameterMap) throws
		JsonProcessingException {
		try{
			CanvasMessageDto canvasMessageDto = new CanvasMessageDto();
			log.info("=== 캔버스 연결 ===");

			canvasMessageDto.setBackground(parameterMap.get("background"));
			canvasMessageDto.setImage(parameterMap.get("image"));
			canvasMessageDto.setType(parameterMap.get("type"));
			canvasMessageDto.setChannelSeq(Long.valueOf(parameterMap.get("channelSeq")));
			canvasMessageDto.setObject(parameterMap.get("object"));
			canvasMessageDto.setVideo(parameterMap.get("video"));
			Long userSeq = Long.valueOf(parameterMap.get("userSeq"));

			log.info("==== 캔버스 변경 pub 요청 ===");
			log.info("입력받은 캔버스 정보 : ");
			log.info(canvasMessageDto+"");
			CanvasDto canvasDto = canvasService.findCanvasByChannelSeq(canvasMessageDto.getChannelSeq());

			if(canvasDto == null){
				canvasService.insertCanvas(canvasDto);
			}else{
				canvasMessageDto.setId(canvasDto.getId());
				canvasService.updateCanvas(canvasMessageDto);
			}

			canvasService.sendCanvasMessage(canvasMessageDto.toJson());
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@GetMapping("/canvas/view")
	public String canvasView() {
		return "canvas";
	}
}
