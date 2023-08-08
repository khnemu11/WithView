import React, { useState, useEffect } from "react";
import "../css/serverenter.css";
import axios from "axios";
import { useSelector } from "react-redux";
import { useParams, useNavigate } from "react-router-dom";



const ServerEnter = () => {
  const [serverImage, setServerImage] = useState("/withView2.png");
  const [serverName, setServerName] = useState("기본 서버 이름");
  const [serverMembersNumber, setServerMembersNumber] = useState(0); // Dummy members number, replace it with actual value
  const [serverSeq, setServerSeq] = useState('');
  const userSeq = useSelector((state) => state.user.seq);
 
  const { inviteLink } = useParams(); // 라우터의 파라미터 값을 받아옵니다.
  const navigate = useNavigate(); // 라우터로 이동하기 위한 훅

  

  useEffect(() => {
    const fetchServerData = async () => {
      try {
        const response = await axios.get(`https://i9d208.p.ssafy.io/api/invite/${inviteLink}`);
        
        const backgroundImageUrl = `https://dm51j1y1p1ekp.cloudfront.net/server-background/${response.data.server.backgroundImgSearchName}`;

        setServerImage(backgroundImageUrl);
        setServerName(response.data.server.name);
        setServerSeq(response.data.server.seq);
        // setServerMembersNumber(response.data.members); // 기존 코드는 주석 처리합니다. 만약 members 데이터도 받아오게 되면 이 부분을 활성화할 수 있습니다.
      } catch (error) {
        console.error("Error fetching server data", error);
      }
    };

    fetchServerData();
  }, [inviteLink]);

  const handleJoin = async () => {
    // 서버에 POST 요청을 보냅니다.
    try {
      const formData = new FormData();
      formData.append('serverSeq', serverSeq); 
      formData.append('userSeq', userSeq); 

      const response = await axios.post('https://i9d208.p.ssafy.io/api/servers/enter', formData);

      if (response.data.success) { // 성공적으로 서버에 가입했다면
        navigate(`/server/${serverSeq}`); // 해당 서버 페이지로 이동합니다.
      } else {
        alert('이미 가입한 서버입니다.');
      }
    } catch (error) {
      console.error("Error joining server:", error);
      alert('이미 가입한 서버입니다.');
      console.log(userSeq, serverSeq)
    }
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
