import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import "../css/findpassword.css";
import axios from "axios";
import withview from "../assets/withview.png";

export default function FindPassword() {
  const [id, setId] = useState("");
  const [email, setEmail] = useState("");
  const [code, setCode] = useState("");
  const [password, setPassword] = useState("");
  const [password2, setPassword2] = useState("");
  const [isModalActive, setIsModalActive] = useState(false);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const url = "https://i9d208.p.ssafy.io/api";

  function checkFindEmail() {
    axios({
      method: "GET",
      url: `${url}/users/email/validate?email=${email}&id=${id}&var=3`,
    })
      .then((res) => {
        console.log("이메일 전송 완료");
        console.log(res.data);
        alert("이메일로 인증번호를 전송했습니다.");
      })
      .catch((err) => {
        console.log(err);
        if (err.response && err.response.status === 400) {
          alert("아이디와 이메일을 다시 확인해주세요!");
        } else {
          alert("error");
        }
      });
  }
  function changePassword() {
    axios({
      method : "PUT",
      url : `${url}/users/${seq}/password`
    })
  }
  const authenticateCode = (e) => {
    e.preventDefault();
    axios({
      method: "GET",
      url: `${url}/users/email/authenticate?email=${email}&code=${code}`,
    })
      .then((res) => {
        console.log(res.data);
        alert("인증완료!!");
        setIsModalActive(true);
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
                <input
                  className="input findpassword_input"
                  type="email"
                  onChange={(e) => {
                    console.log(e.target.value);
                    setId(e.target.value);
                  }}
                />
              </div>
            </div>
            <div style={{ width: "85%" }}>
              <label className="label findpassword_label">
                메일 주소 입력좀 ㅠㅠ
              </label>
              <div className="findpassword_input_button">
                <input
                  className="input findpassword_input"
                  type="email"
                  onChange={(e) => {
                    console.log(e.target.value);
                    setEmail(e.target.value);
                  }}
                />
                <button
                  className="button findpassword_button"
                  onClick={checkFindEmail}
                >
                  인증메일전송
                </button>
              </div>
            </div>

            <div style={{ width: "85%" }}>
              <label className="label findpassword_label">
                이메일로 발송된 인증번호를 입력해주세요
              </label>
              <div className="findpassword_input_button">
                <input
                  className="input findpassword_input"
                  onChange={(e) => {
                    setCode(e.target.value);
                  }}
                />
                <button
                  className="button findpassword_button"
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
                    <input
                      type="text"
                      className="input"
                      onChange={(e) => {
                        setPassword(e.target.value)
                      }}
                    />

                    <input
                      type="text"
                      className="input"
                      onChange={(e) => {
                        setPassword2(e.target.value)
                      }}
                    />
                  </section>

                  <footer className="modal-card-foot">
                    <button className="button is-info" onClick={changePassword}>비밀번호 변경</button>

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

          <div className="findpassword_findpass">
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
                navigate("/findid");
              }}
            >
              <a>아이디 찾기</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
