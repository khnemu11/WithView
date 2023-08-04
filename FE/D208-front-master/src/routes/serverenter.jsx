// firstmain.jsx
import React, { useState, useEffect } from "react";
import "../css/serverenter.css";
import axios from "axios";

const ServerEnter = () => {
  const [serverImage, setServerImage] = useState("/withView2.png");
  const [serverName, setServerName] = useState("기본 서버 이름");
  const [serverMembersNumber, setServerMembersNumber] = useState(0); // Dummy members number, replace it with actual value

  useEffect(() => {
    const fetchServerData = async () => {
      try {
        // Replace with your server API endpoint
        const response = await axios.get("your-api-url-here");
        setServerImage(response.data.image || "/withView2.png");
        setServerName(response.data.name);
        setServerMembersNumber(response.data.members);
      } catch (error) {
        console.error("Error fetching server data", error);
      }
    };

    fetchServerData();
  }, []);

  const handleJoin = () => {
    // Handle join server event here
  };

  return (
    <div className="mainbox">
      <div className="serverEnterMain">
        <div className="serverEnterText">서버에 초대 받았어요 !</div>
        <img src={serverImage} alt="Server" className="serverEnterImage" />
        <div className="serverNameText">{serverName}</div>
        <div className="serverMembersIcon">
          <span className="membersIcon"></span>
          <span className="serverMembersSpan">{serverMembersNumber}</span>
        </div>
        <button
          className="button mt-2 has-text-white serverEnterButton"
          onClick={handleJoin}
        >
          참가하기
        </button>
      </div>
    </div>
  );
};

export default ServerEnter;
