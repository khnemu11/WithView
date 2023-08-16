import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
// import { useCookies } from "react-cookie";
import { setToken } from "../redux/actions/tokenActions";
import "../css/Login.css";
import axios from "axios";
import withview from "../assets/withview.png";
import { setUser } from "../redux/actions/userActions";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { setStomp } from "../redux/actions/stompActions";
axios.defaults.withCredentials = true;

export default function Test() {
  const [Id, setId] = useState("");
  const [password, setPassword] = useState("");
  // const [cookies, setCookie] = useCookies(""); // 쿠키 훅
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const url = "https://i9d208.p.ssafy.io/api";

  useEffect(() => {
    // 페이지 진입 시 페이지 전환 애니메이션 클래스 추가...
    const loginElement = document.querySelector(".login_wrap");

    if (loginElement) {
      loginElement.classList.remove("login_transition-enter-active");
      loginElement.classList.add("login_transition-enter");
      setTimeout(() => {
        loginElement.classList.remove("login_transition-enter");
        loginElement.classList.add("login_transition-enter-active");
      }, 100); // 애니메이션 시간과 일치하는 시간으로 설정 (0.1초)
    }
  }, []);

  const handleKeyPress = (e) => {
    if (e.key === "Enter") {
      checkLogin();
    }
  };

  function checkLogin() {
    axios({
      method: "POST",
      url: `${url}/login/login`,
      data: { id: Id, password: password },
    })
      .then((res) => {
        if (res.data.success) {
          console.log("토큰 받아라");
          console.log(res.data);
          const accessToken = res.data.AccessToken.accessToken;
          const userInfo = {
            seq: res.data.UserInfo.seq,
            nickname: res.data.UserInfo.nickname,
            profile: res.data.UserInfo.profileImgSearchName,
          };

          // 토큰을 Redux로 저장
          dispatch(setToken(accessToken));
          dispatch(setUser(userInfo));
          const sock = new SockJS(`${url}/ws-stomp`); // 연결되는 웹소켓의 end-point
          const stomp = Stomp.over(sock); // Stomp 연결
          stomp.connect(
            {
              userSeq: res.data.UserInfo.seq,
            },
            (res) => {
              console.log(res);
              // dispatch(setStomp(stomp))
            },
            (err) => {
              console.log(err);
            }
          );
          dispatch(setStomp(stomp));
          // 로비화면으로 이동
          navigate("/mainpage");
        } else {
          if (res.data.message === "이미 로그인 중인 계정입니다.") {
            const logoutcheck = confirm(
              "이미 로그인 중인 계정입니다. 기존 계정을 로그아웃 할까요?"
            );
            if (logoutcheck) {
              axios({
                method: "POST",
                url: `${url}/login/logout2`,
                data: {
                  id: Id,
                },
              })
                .then((res) => {
                  console.log(res.data);
                  checkLogin();
                })
                .catch((err) => {
                  console.log(err);
                });
            }
          } else {
            alert(res.data.message);
          }

          setId("");
          setPassword("");
        }
      })
      .catch((err) => {
        console.log(err);
        alert("다시 입력해 주세요.");
        setId("");
        setPassword("");
      });
  }
  return (
    <div className="login_first">
      <div className="login_wrap">
        <div className="login_base columns is-multiline">
          <div className="login_title column is-8">돌아오셔서 반가워요!</div>
          <div className="login_img column is-4">
            <img
              style={{ width: "15vw", height: "15vw", marginTop: "30px" }}
              src={withview}
              alt="그림 없음"
            />
          </div>
          <div className="login_inputs column is-8">
            <div>
              <p className="login_label">아이디를 입력해 주세요 !</p>
              <input
                className="login_input input"
                type="text"
                value={Id}
                onChange={(e) => {
                  setId(e.target.value);
                }}
                onKeyDown={handleKeyPress}
              />
            </div>
            <div>
              <p className="login_label">비밀번호를 입력해 주세요 !</p>
              <input
                className="login_input input"
                type="password"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value);
                }}
                onKeyDown={handleKeyPress}
              />
            </div>
          </div>
          <div className="login_buttons column is-8">
            <button
              className="button login_button column is-full"
              onClick={checkLogin}
            >
              로그인
            </button>
          </div>
          <div className="login_buttons column is-4">
            <button
              className="button login_button column is-full"
              onClick={() => {
                const loginElement = document.querySelector(".login_wrap");

                if (loginElement) {
                  loginElement.classList.add("login_transition-exit-active");
                  setTimeout(() => {
                    navigate("/signup");
                  }, 200); // 애니메이션 시간과 일치하는 시간으로 설정 (0.3초)
                }
              }}
            >
              회원가입
            </button>
          </div>
          <div className="login_findes column">
            <div style={{ marginRight: "35px" }}>
              <a
                onClick={() => {
                  navigate("/findid");
                }}
              >
                아이디 찾기
              </a>
            </div>
            |
            <div style={{ marginLeft: "35px" }}>
              <a
                onClick={() => {
                  navigate("/findpassword");
                }}
              >
                비밀번호 찾기
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
