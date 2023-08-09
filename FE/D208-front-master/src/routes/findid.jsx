import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import "../css/findid.css";
import axios from "axios";
import withview from "../assets/withview.png";

export default function FindId() {
  const [email, setEmail] = useState("");
  const [code, setCode] = useState("");
  const [maskedId, setMaskedId] = useState("");
  const [isModalActive, setIsModalActive] = useState(false);
  
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const url = "https://i9d208.p.ssafy.io/api";

  function checkFindEmail() {
    axios({
      method: "GET",
      url: `${url}/users/email/validate?email=${email}&var=2`,
    })
      .then((res) => {
        console.log(res.data);
        if (res.data.success) {
          alert("이메일로 인증번호를 전송했습니다!!");
          console.log("이메일 전송 완료");
        } else {
          alert("이메일 전송 실패");
          setEmail("");
        }
      })
      .catch((err) => {
        console.log(err);
        alert("다시 입력해 주세요.");
        setEmail("");
      });
  }

  const authenticateCode = (e) => {
    e.preventDefault();
    axios({
      method: "GET",
      url: `${url}/users/email/authenticate?email=${email}&code=${code}&var=2`,
    })
      .then((res) => {
        console.log(res.data);
        setMaskedId(res.data.id)
        alert("인증완료!!");
        setIsModalActive(true)
        
      })
      .catch((err) => {
        if (err.response && err.response.status === 401) {
          alert("인증번호를 다시 확인해주세요");
        } else {
          alert("error");
        }
        console.log(err);
      });
  };

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
                제발 메일 주소 입력좀 
              </label>
              <div className="findid_input_button">
                <input
                  className="input findid_input"
                  type="email"
                  onChange={(e) => {
                    console.log(e.target.value);
                    setEmail(e.target.value);
                  }}
                />
                <button
                  className="button findid_button"
                  onClick={checkFindEmail}
                >
                  인증메일전송
                </button>
              </div>
            </div>

            <div style={{ width: "85%" }}>
              <label className="label findid_label">
                이메일로 발송된 인증번호를 입력해주세요
              </label>
              <div className="findid_input_button">
                <input
                  className="input findid_input"
                  onChange={(e) => {
                    setCode(e.target.value);
                  }}
                />
                <button
                  className="button findid_button"
                  onClick={authenticateCode}
                >
                  인증번호입력
                </button>
              </div>

              <div
                className={`modal ${isModalActive ? "is-active" : ""}`}
                id="myModal"
              >
                <div
                  className={`modal-background ${
                    isModalActive ? "is-active" : ""
                  }`}
                  onClick={() => {
                    setIsModalActive(false);
                  }} // 모달 배경 클릭 시 모달 닫기
                ></div>
                <div className="modal-card">
                  <header className="modal-card-head">
                    <p className="modal-card-title">가입하신 아이디 입니다.</p>
                    <button
                      className="delete"
                      aria-label="close"
                      onClick={() => {
                        setIsModalActive(false);
                      }} // 모달 닫기 버튼 클릭 시 setIsModalActive(false) 호출
                    ></button>
                  </header>

                  <section className="modal-card-body">
                    <h4 className="title">아이디 : {maskedId}</h4>
                  </section>

                  <footer className="modal-card-foot">
                    {/* <button
                      className="button is-info"
                      onClick={authenticateCode}
                    >
                      인증 확인
                    </button> */}

                    <button
                      className="button"
                      onClick={() => {
                        setIsModalActive(false);
                      }}
                    >
                      Cancel
                    </button>
                  </footer>
                </div>
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
            |{" "}
            <div
              onClick={() => {
                navigate("/findpassword");
              }}
            >
              <a>비밀번호 찾기</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
