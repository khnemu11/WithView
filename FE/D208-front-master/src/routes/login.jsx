import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { setToken } from "../redux/actions/tokenActions";
import "../css/Login.css";
import axios from "axios";
import withview from "../assets/withview.png";
import { setUser } from "../redux/actions/userActions";




export default function Login() {
  const [Id, setId] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const url = "https://i9d208.p.ssafy.io/api";
  
  // redux 저장 테스트용
  const checktoken = useSelector((state) => state.token);
  const userInfotest = useSelector((state) => state.user);
  useEffect(() => {
    console.log(checktoken); // 토큰 값이 변경될 때마다 출력...
    console.log(userInfotest); // 토큰 값이 변경될 때마다 출력...
  }, [checktoken,userInfotest]);
  //


  const handleKeyPress = (e) => {
    if (e.key === "Enter") {
      checkLogin();
    }
  };

  function setSessionCookie(name, value) {
    // 쿠키 만료 시간을 현재 시간으로부터 1시간으로 설정합니다. (세션 쿠키로 만들기 위해 시간을 지정하지 않습니다.)
    const expires = new Date(Date.now() + 3600000).toUTCString();
  
    // 쿠키를 생성합니다.
    document.cookie = `${name}=${encodeURIComponent(value)}; expires=${expires}; path=/`;
  }
  
  function getSessionCookie(name) {
    // 쿠키를 읽어옵니다.
    const cookies = document.cookie.split(";").map(cookie => cookie.trim());
    for (const cookie of cookies) {
      if (cookie.startsWith(`${name}=`)) {
        return decodeURIComponent(cookie.substring(name.length + 1));
      }
    }
    return null;
  }

  function deleteSessionCookie(name) {
    // 쿠키를 삭제하려면 쿠키 만료 시간을 현재 시간으로 설정합니다.
    const expires = new Date(0).toUTCString();
    document.cookie = `${name}=; expires=${expires}; path=/`;
  }

  

  function checkLogin() {
    axios({
      method: "POST",
      url: `${url}/login/login`,
      data: { id: Id, password: password },
    })
      .then((res) => {
        if (res.data.success) {
          console.log("토큰 받아라");
          const accessToken = res.data.JWT.accessToken;
          const refreshToken = res.data.JWT.refreshToken;
          const userInfo = {seq : res.data.UserInfo.seq, nickname : res.data.UserInfo.nickname, profile : res.data.UserInfo.profileImgSearchName}

          console.log(res.data);
          // 토큰을 Redux로 저장
          dispatch(setToken(accessToken));
          dispatch(setUser(userInfo));
          // refreshToken 저장하기
          setSessionCookie("refreshToken", refreshToken);
          // 로비화면으로 이동
          navigate("/mainpage");
        } else {
          alert("로그인 실패!!");
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
      <div className="login_login">
        <div className="login_titleWrap">
          <div className="login_inputs">
            <div className="login_inputWrap">
              <div className="login_hello">돌아오셔서 반가워요 !</div>

              <div>
                <label className="label login_label">
                  아이디를 입력해주세요 !
                </label>
                <input
                  type="text"
                  className="input login_input"
                  value={Id}
                  onChange={(e) => {
                    setId(e.target.value);
                  }}
                  onKeyDown={handleKeyPress}
                />
              </div>

              <div>
                <label className="label login_label">
                  비밀번호를 입력해주세요 !
                </label>
                <input
                  type="password"
                  className="input login_input"
                  value={password}
                  onChange={(e) => {
                    setPassword(e.target.value);
                  }}
                  onKeyDown={handleKeyPress}
                />
              </div>
            </div>

            <div className="login_input2Wrap">
              <div>
                <img src={withview} alt="그림없음" />
              </div>
            </div>
          </div>

          <div className="login_buttons">
            <button
              className="button login_button"
              style={{ width: "55%", marginRight: "10px" }}
              onClick={checkLogin}
            >
              로그인
            </button>
            <button
              className="button login_button"
              onClick={() => {
                navigate("/signup");
              }}
              style={{ width: "25%", marginRight: "35px" }}
            >
              회원가입
            </button>
          </div>

          <div className="login_findes">
            <div style={{ marginRight: "30px" }}>
              <a onClick={()=>{
                navigate("/findid")
              }}>아이디 찾기</a>
            </div>
            |
            <div style={{ marginLeft: "30px" }}>
              <a onClick={()=>{
                navigate("/findpassword")
              }}>비밀번호 찾기</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
