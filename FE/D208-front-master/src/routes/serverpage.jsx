import React, { useState, useEffect } from "react";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import "../css/firstmain.css";
import "../css/mainpage.css"; // CSS 파일 임포트
import "../css/serverpage.css";
import ServerOptions from "./components/serveroptions";

const Serverpage = () => {
  const [profileImage, setProfileImage] = useState("/프사.png");
  const [profileNickname, setProfileNickname] = useState("기본 닉네임");

  const [serverName, setServerName] = useState("기본 서버 이름");

  return (
    <div className="mainbox">
      <div className="innermain">
        <ServerOptions
          profileImage={profileImage}
          profileName={profileNickname}
        />
        <hr className="serverOptionsLine_main" />
        <div className="backAndServerNameContainer">
          <img
            className="backArrowIcon"
            src="/backarrow.png"
            alt="Go Back"
            onClick={() => window.history.back()}
          />
          <h1 className="serverNameText">{serverName}</h1>
        </div>
      </div>
    </div>
  );
};

export default Serverpage;
