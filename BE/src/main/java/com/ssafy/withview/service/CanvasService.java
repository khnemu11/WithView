package com.ssafy.withview.service;

import com.ssafy.withview.dto.CanvasDto;

public interface CanvasService {
	public void insertCanvas(CanvasDto canvasDto);
	public void updateCanvas(CanvasDto canvasDto);
	public void deleteCanvasByChannelSeq(Long channelSeq);
	public CanvasDto findCanvasByChannelSeq(Long channelSeq);
}
