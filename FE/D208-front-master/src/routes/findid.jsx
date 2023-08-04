import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import "../css/findid.css";
import axios from "axios";
import withview from "../assets/withview.png";

export default function FindId() {
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
    <div className="findid_first">
      <div className="findid_findid">
        <div className="findid_titleWrap">
          <div className="findid_title">
            <div className="findid_title_word">
              <p className="findid_hello">아이디를 잊으셨나요 ?</p>
              <p className="findid_hello2">
                가입할때 입력하신 메일주소로 아이디를 찾으실 수 있어요!
              </p>
            </div>
            <div className="findid_title_logo">
              <img src={withview} alt="그림없음" />
            </div>
          </div>

          <div className="findid_inputs">
            <div style={{ width: "85%" }}>
              <label className="label findid_label">
                메일 주소 입력좀 ㅅㅂ
              </label>
              <div className="findid_input_button">
                <input className="input findid_input" 
                   type="email" 
                   onChange={(e)=>{
                    console.log(e.target.value)
                    setEmail(e.target.value)
                   }}
                />
                <button className="button findid_button"
                    onClick={checkFindEmail}
                >인증메일전송</button>
              </div>
            </div>

            <div style={{ width: "85%" }}>
              <label className="label findid_label">
                이메일로 발송된 인증번호를 입력해주세요
              </label>
              <div className="findid_input_button">
                <input className="input findid_input" />
                <button className="button findid_button">인증번호입력</button>
              </div>
            </div>
          </div>

          <div className="findid_findpass">
            <div
              onClick={() => {
                navigate("/login");
              }}
            >
              <a>뒤로가기</a>
            </div>
            | <div><a>비밀번호 찾기</a></div>
          </div>
        </div>
      </div>
    </div>
  );
}
