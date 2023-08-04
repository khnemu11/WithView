import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { setToken } from "../redux/actions/tokenActions";
import "../css/Login.css";
import axios from "axios";
import withview from "../assets/withview.png";
import { setUser } from "../redux/actions/userActions";
import { Button } from 'react-bootstrap';



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
          sessionStorage.setItem("refreshToken", refreshToken);
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
                  이메일을 입력해주세요 !
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
            <Button
              className="button login_button"
              style={{ width: "55%", marginRight: "10px" }}
              onClick={checkLogin}
            >
              로그인
            </Button>
            <Button
              className="button login_button"
              onClick={() => {
                navigate("/signup");
              }}
              style={{ width: "25%", marginRight: "35px" }}
            >
              회원가입
            </Button>
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
