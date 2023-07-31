import React from "react";

const ServerOptions = ({ profileImage, profileNickname }) => (
  <div className="serverOptions is-flex is-align-items-center">
    <img className="withViewImageTop" src="/withView.png" alt="With View" />
    <div className="is-flex">
      <p className="serverOptionText">서버 목록</p>
      <p className="serverOptionText">서버 추가</p>
      <p className="serverOptionText">내 친구</p>
      <p className="serverOptionText">정보 수정</p>
      <p className="serverOptionText">창작마당</p>
    </div>
    <div className="ml-auto is-flex">
      <img src={profileImage} className="profile-bar-image" alt="Profile" />
      <p className="profile-name">{profileNickname}</p>
    </div>
  </div>
);

export default ServerOptions;
