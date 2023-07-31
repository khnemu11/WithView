// firstmain.jsx
import React, { useState } from "react";
import "../../css/firstmain.css"; // CSS 파일 임포트
import ServerOptions from "./serveroptions";

const FirstMain = () => {
  const [profileImage, setProfileImage] = useState("/프사.png");
  const [profileNickname, setProfileNickname] = useState("기본 닉네임");

  return (
    <div className="mainbox">
      <div className="innerfirstmain">
        <ServerOptions
          profileImage={profileImage}
          profileNickname={profileNickname}
        />

        <hr className="serverOptionsLine" />
        <img className="withViewImage" src="/withView.png" alt="With View" />
        <div className="content">
          <p>WithView에 오신 걸 환영합니다!</p>
          <p>다양한 사람들과의 만남과 이야기를 즐겨보세요.</p>
        </div>
        <div className="columns is-centered is-gapless is-vcentered m-4">
          <div className="column mr-3">
            <img
              className="userplusImage"
              src="/userplus.png"
              alt="User Plus"
            />
            <p className="addFriendText">친구를 추가해보세요 !</p>
          </div>
          <div className="column ml-3">
            <img
              className="serverplusImage"
              src="/serverplus.png"
              alt="Server Plus"
            />
            <p className="addServerText">서버를 만들어보세요 !</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FirstMain;
