import React from "react";
import { Link, useNavigate } from "react-router-dom";

const ServerOptions = ({ profileImage, profileNickname }) => {
  const navigate = useNavigate();
  const openFriendListInNewWindow = () => {
    window.open("/friendlist", "_blank", "width=900,height=600");
  };

  return (
    <div className="serverOptions is-flex is-align-items-center">
      <img
        className="withViewImageTop"
        src="/withView.png"
        alt="With View"
        onClick={() => navigate("/mainpage")}
        style={{ cursor: "pointer" }}
      />
      <div className="is-flex">
        <Link to="/mainpage" className="serverOptionText">
          서버 목록
        </Link>
        <Link to="/serverplus" className="serverOptionText">
          서버 추가
        </Link>
        <p className="serverOptionText" onClick={openFriendListInNewWindow} style={{ cursor: "pointer" }}>
          내 친구
        </p>
        <Link to="/profile" className="serverOptionText">
          정보 수정
        </Link>
        <Link to="/board" className="serverOptionText">
          창작마당
        </Link>
      </div>
      <div className="ml-auto is-flex">
        <img src={profileImage} className="profile-bar-image" alt="Profile"  onClick={()=>navigate('/profile')} style={{ cursor: "pointer" }}/>
        <p className="profileNameTop">{profileNickname}</p>
      </div>
    </div>
  );
};

export default ServerOptions;
