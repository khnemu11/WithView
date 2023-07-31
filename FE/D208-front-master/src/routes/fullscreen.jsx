import { useState, useEffect, useRef } from "react";
import { Link } from "react-router-dom";
import x from "../assets/x.png";
import fs_style from "../css/fullscreen.module.css";

export default function GroupChat() {
  const canvasRef = useRef(null);
  const [inputText, setInputText] = useState("");
  const [chatLog, setChatLog] = useState([]);

  useEffect(() => {
    // canvas 요소를 가져옵니다.
    const canvas = canvasRef.current;
    // 2D context를 생성합니다.
    const ctx = canvas.getContext("2d");

    // 텍스트 스타일을 설정합니다.
    ctx.font = "20px Arial";
    ctx.fillStyle = "black";

    // 텍스트를 그립니다.
    ctx.fillText("공유하는 화면, withview !", 10, 50);
  }, []);

  const handleInputChange = (event) => {
    setInputText(event.target.value);
  };

  const handleTempButtonClick = () => {
    setChatLog((prevChatLog) => [...prevChatLog, inputText]);
    setInputText(""); // 입력 후 인풋 초기화
  };

  return (
    <>
      {/* 전체 화면 */}
      <div className="groupchat">
        {/* 캔버스 화면 */}
        <canvas className={fs_style.canvas} ref={canvasRef}></canvas>
        {/* 채팅창 */}
        <div className={fs_style.chat}>
          <div className={fs_style.chatlog}>
            {/* 채팅 로그 출력 */}
            {chatLog.map((message, index) => (
              <div key={index}>{message}</div>
            ))}
          </div>
          {/* 임시 인풋 */}
          <div>
            <input type="text" value={inputText} onChange={handleInputChange} />
            <button onClick={handleTempButtonClick}>temp</button>
          </div>
        </div>
        {/* 얼굴 */}
        <div className={fs_style.face}>
          <h1>얼굴들</h1>
        </div>
        {/* 나가기 */}
        <div className={fs_style.exit}>
          <Link to={"/"}>
            <img src={x} alt="" className={fs_style.x} />
          </Link>
        </div>
      </div>
    </>
  );
}
