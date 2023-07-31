import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../css/Signup.css";
import axios from "axios";

export default function Signup() {
  const [email, setEmail] = useState("");
  const [password1, setPassword1] = useState("");
  const [id, setId] = useState("");
  const [password2, setPassword2] = useState("");
  const [nickname, setNickname] = useState("");

  const [checkPass, setCheckPass] = useState(false);
  const [checkID, setCheckID] = useState(false);
  const [checkPass2, setCheckPass2] = useState(false);
  const [checkEmail, setCheckEmail] = useState(false);
  const [checkNickname, setCheckNickname] = useState(false);

  const [idLabel, setIdLabel] = useState("아이디");
  const [passwordLabel, setPasswordLabel] = useState("비밀번호");
  const [passwordLabel2, setPasswordLabel2] = useState("비밀번호 확인");
  const [nicknameLabel, setNicknameLabel] = useState("닉네임");
  const [idLabelColor, setIdLabelColor] = useState({ color: "#80C4FF" });
  const [passwordLableColor, setPasswordLableColor] = useState({
    color: "#80C4FF",
  });
  const [passwordLableColor2, setPasswordLableColor2] = useState({
    color: "#80C4FF",
  });
  const [nicknameLabelColor, setNicknameLabelColor] = useState({
    color: "#80C4FF",
  });
  const [buttonDisabled, setButtonDisabled] = useState(false);
  const [buttonDisabled2, setButtonDisabled2] = useState(false);

  useEffect(() => {
    // 페이지 진입 시 페이지 전환 애니메이션 클래스 추가...
    document.body.classList.remove("signup_transition-enter-active");
    document.body.classList.add("signup_transition-enter");
    // 페이지 진입 시 활성화 애니메이션 클래스 추가

    // 애니메이션이 끝난 후에 활성화 애니메이션 클래스 제거
    setTimeout(() => {
      document.body.classList.remove("signup_transition-enter");
      document.body.classList.add("signup_transition-enter-active");
    }, 300); // 애니메이션 시간과 일치하는 시간으로 설정 (0.3초)
  }, []);

  useEffect(() => {
    // 아이디 필드가 변경되었을 때 실행되는 함수
    if (id === "" || idLabel === "아이디를 다시한번 확인해주세요!") {
      setButtonDisabled(true); // 아이디 빈칸 또는 유효성 검사 실패 시 버튼 비활성화
    } else {
      setButtonDisabled(false); // 그 외의 경우 버튼 활성화
    }
  }, [id, idLabel]);

  useEffect(() => {
    if (!checkPass || !checkPass2 || !checkNickname) {
      setButtonDisabled2(true);
    } else {
      setButtonDisabled2(false);
    }
  }, [checkPass, checkPass2, checkEmail, checkID, checkNickname]);

  useEffect(() => {
    if (password1 === password2 && password2 !== "") {
      setPasswordLabel2("비밀번호 확인 완료!");
      setPasswordLableColor2({ color: "#80C4FF" });
      setCheckPass2(true);
    } else if (password2 === "") {
      setPasswordLabel2("비밀번호 확인");
      setPasswordLableColor2({ color: "#80C4FF" });
      setCheckPass2(false);
    } else if (password1 === "") {
      setPasswordLabel("비밀번호");
      setPasswordLableColor({ color: "#80C4FF" });
      setCheckPass(false);
    } else {
      setPasswordLabel2("입력한 비밀번호와 일치하지 않습니다!");
      setPasswordLableColor2({ color: "red" });
      setCheckPass2(false);
    }
  }, [password1, password2]);

  const navigate = useNavigate();
  const url = "https://i9d208.p.ssafy.io/:9091/api/";

  const checkSignUp = (e) => {
    e.preventDefault();
    axios({
      method: "POST",
      url: `${url}/users`,
      data: { id, password1, nickname, email },
    })
      .then((res) => {
        console.log(res);
        navigate("/login");
      })
      .catch((err) => {
        // console.log(id)
        console.log(err);
        alert("회원가입에 실패하였습니다!");
      });
  };

  const overlapCheck = (e) => {
    e.preventDefault();
    axios({
      method: "GET",
      url: `${url}/users/check-id`,
      data: { id },
    })
      .then((res) => {
        console.log(res);
        setCheckID(true);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  return (
    <div className="signup_first">
      <div className="signup_signup">
        <div className="signup_titleWrap">
          <div className="signup_hello">환영합니다 !</div>
          <div className="signup_inputwrap">
            <form className="signup_form" action="#">
              <div style={{ width: "85%" }}>
                <label className="label signup_label" style={idLabelColor}>
                  {idLabel}
                </label>

                <div className="signup_inputs_div">
                  <input
                    type="text"
                    className="input signup_id_email_input"
                    value={id}
                    placeholder="5~20자, 영어와 숫자만 가능, 빈칸 X"
                    onChange={(e) => {
                      setId(e.target.value);
                      console.log(e.target.value);

                      if (
                        e.target.value.includes(" ") ||
                        e.target.value.length < 5 ||
                        e.target.value.length > 20 ||
                        !/^[A-Za-z0-9]*$/.test(e.target.value)
                      ) {
                        setIdLabel("아이디를 다시한번 확인해주세요!");
                        setIdLabelColor({ color: "red" });
                      } else {
                        setIdLabel("중복 확인 가능!");
                        setIdLabelColor({ color: "#80C4FF" });
                      }
                    }}
                  />
                  <button
                    className="button is-info signup_inputs_buttons"
                    onClick={overlapCheck}
                    disabled={buttonDisabled}
                  >
                    중복 확인
                  </button>
                </div>
              </div>

              <div style={{ width: "85%" }}>
                <label className="label signup_label">이메일</label>

                <div className="signup_inputs_div">
                  <input
                    type="email"
                    className="input signup_id_email_input"
                    value={email}
                    onChange={(e) => {
                      setEmail(e.target.value);
                    }}
                  />
                  <button className="button signup_inputs_buttons">
                    인증메일발송
                  </button>
                </div>
              </div>

              <div style={{ width: "85%" }}>
                <label
                  className="label signup_label"
                  style={passwordLableColor}
                >
                  {passwordLabel}
                </label>
                <input
                  type="password"
                  className="input signup_input"
                  placeholder="8~16자, 특수문자 최소 1자 포함, 빈칸 X"
                  value={password1}
                  onChange={(e) => {
                    setPassword1(e.target.value);
                    if (
                      e.target.value.includes(" ") ||
                      !/^(?=.*[a-zA-Z])(?=.*[!@#$%^&*()\-_=+[\]{};:'",.<>/?\\|~`])[a-zA-Z\d!@#$%^&*()\-_=+[\]{};:'",.<>/?\\|~`]{8,16}$/.test(
                        e.target.value
                      )
                    ) {
                      setPasswordLabel("사용할 수 없는 비밀번호 입니다!");
                      setPasswordLableColor({ color: "red" });
                      setCheckPass(false);
                    } else {
                      setPasswordLabel("비밀번호 사용 가능!");
                      setPasswordLableColor({ color: "#80C4FF" });
                      setCheckPass(true);
                    }
                  }}
                />
              </div>

              <div style={{ width: "85%" }}>
                <label
                  className="label signup_label"
                  style={passwordLableColor2}
                >
                  {passwordLabel2}
                </label>
                <input
                  type="password"
                  className="input signup_input"
                  value={password2}
                  onChange={(e) => {
                    setPassword2(e.target.value);
                  }}
                />
              </div>

              <div style={{ width: "85%" }}>
                <label
                  className="label signup_label"
                  style={nicknameLabelColor}
                >
                  {nicknameLabel}
                </label>
                <input
                  type="text"
                  className="input signup_input"
                  placeholder="2~6자, 특수문자 x"
                  value={nickname}
                  onChange={(e) => {
                    setNickname(e.target.value);

                    if (
                      e.target.value.includes(" ") ||
                      !/^[\u3131-\u318E\uAC00-\uD7A3a-zA-Z\d]{2,6}$/.test(
                        e.target.value
                      )
                    ) {
                      setNicknameLabel("사용할수 없는 닉네임 입니다!");
                      setNicknameLabelColor({ color: "red" });
                      setCheckNickname(false);
                    } else {
                      setNicknameLabel("사용할수 있는 닉네임 입니다!");
                      setNicknameLabelColor({ color: "#80C4FF" });
                      setCheckNickname(true);
                    }
                  }}
                />
              </div>
              <div
                style={{
                  width: "85%",
                  display: "flex",
                  justifyContent: "center",
                }}
              >
                <button
                  className="signup_button button is-info button"
                  onClick={checkSignUp}
                  disabled={buttonDisabled2}
                >
                  회원가입
                </button>
              </div>
            </form>
          </div>
          <div
            className="signup_back"
            onClick={() => {
              navigate("/login");
            }}
          >
            이전 화면으로 돌아가기
          </div>
        </div>
      </div>
    </div>
  );
}