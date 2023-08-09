// firstmain.jsx
import React, { useState, useEffect } from "react";
import "../../css/firstmain.css"; // CSS 파일 임포트
import ServerOptions from "./serveroptions";
import { useSelector } from "react-redux";


const FirstMain = () => {
  const [profileImage, setProfileImage] = useState(null);
  // const [profileNickname, setProfileNickname] = useState("기본 닉네임");
  const profileNickname = useSelector((state) => state.user.nickname)
  const profileImageURL = useSelector((state) => state.user.profile);
  const profileImageUrl = `https://dm51j1y1p1ekp.cloudfront.net/profile/${profileImageURL}`;


  useEffect(() => {
    // 만약 redux에서 프로필 이미지가 null이면 기본 이미지로 설정
    if (profileImageURL === null) {
      setProfileImage("/withView2.png");
    } else {
      setProfileImage(profileImageUrl);
    }
  }, [profileImageURL]);


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
