import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import "../css/findpassword.css";
import axios from "axios";
import withview from "../assets/withview.png";

export default function FindPassword() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const url = "https://i9d208.p.ssafy.io/api";

  
  function checkFindEmail() {
    axios({
      method: "GET",
      url: `${url}/validate/email-id`,
      data: { eamil : email },
    })
      .then((res) => {
        if (res.data.success) {
          console.log("이메일 전송 완료");
          
          console.log(res.data);
        } else {
          alert("이메일 전송 실패");
          setEmail("");
        }
      })
      .catch((err) => {
        console.log(err);
        alert("다시 입력해 주세요.");
        setEmail("")
      });
  }

  return (
    <div className="findpassword_first">
      <div className="findpassword_findpassword">
        <div className="findpassword_titleWrap">
          <div className="findpassword_title">
            <div className="findpassword_title_word">
              <p className="findpassword_hello">비밀번호를 잊으셨나요 ?</p>
              <p className="findpassword_hello2">
                아이디와 메일주소로 비밀번호를 찾으실 수 있어요!
              </p>
            </div>
            <div className="findpassword_title_logo">
              <img src={withview} alt="그림없음" />
            </div>
          </div>

          <div className="findpassword_inputs">
            <div style={{ width: "85%" }}>
              <label className="label findpassword_label">
                아이디 입력좀 해라 진짜
              </label>
              <div className="findpassword_input_button">
                <input className="input findpassword_input" 
                   type="email" 
                   onChange={(e)=>{
                    console.log(e.target.value)
                    setEmail(e.target.value)
                   }}
                />
              </div>
            </div>
            <div style={{ width: "85%" }}>
              <label className="label findpassword_label">
                메일 주소 입력좀 ㅅㅂ
              </label>
              <div className="findpassword_input_button">
                <input className="input findpassword_input" 
                   type="email" 
                   onChange={(e)=>{
                    console.log(e.target.value)
                    setEmail(e.target.value)
                   }}
                />
                <button className="button findpassword_button"
                    onClick={checkFindEmail}
                >인증메일전송</button>
              </div>
            </div>

            <div style={{ width: "85%" }}>
              <label className="label findpassword_label">
                이메일로 발송된 인증번호를 입력해주세요
              </label>
              <div className="findpassword_input_button">
                <input className="input findpassword_input" />
                <button className="button findpassword_button">인증번호입력</button>
              </div>
            </div>
          </div>

          <div className="findpassword_findpass">
            <div
              onClick={() => {
                navigate("/login");
              }}
            >
              <a>뒤로가기</a>
            </div>
            | <div
              onClick={() => {
                navigate("/findid");
              }}
            ><a>아이디 찾기</a></div>
          </div>
        </div>
      </div>
    </div>
  );
}
