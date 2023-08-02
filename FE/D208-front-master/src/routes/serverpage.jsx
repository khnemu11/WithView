import React, { useState, useEffect } from "react";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import "../css/firstmain.css";
import "../css/mainpage.css"; // CSS 파일 임포트
import "../css/serverpage.css";
import ServerOptions from "./components/serveroptions";
import axios from "axios";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";


const ChannelCard = ({ channel }) => {
  return (
    <div className="channelCard">
      <div className="channelCard-imageContainer">
        <img
          src={`https://dm51j1y1p1ekp.cloudfront.net/channel-background/${channel.backgroundImgSearchName}`}
          alt={channel.name}
          className="channelCard-image"
        />
      </div>
      <div className="channelCard-content">
        <p className="channelCard-title">{channel.name}</p>
      </div>
    </div>
  );
};

const CreateChannelCard = ({ onClick }) => {
  return (
    <div className="channelCard" onClick={onClick}>
      <div className="channelCard-content">
        <p className="channelCard-title">+ Create new channel</p>
      </div>
    </div>
  );
};

const Member = ({ member }) => {
  return (
    <div className="member">
      <img src={member.image} alt={member.name} className="memberImage" />
      <p>{member.name}</p>
    </div>
  );
};

const Collapsible = ({ title, children }) => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="collapsible-server">
      <div className="collapsibleHeader" onClick={() => setIsOpen(!isOpen)}>
        {title}
        <img 
          src={isOpen ? "/nav arrow up.png" : "/nav arrow down.png"} 
          alt="Toggle" 
          className="collapsibleIcon"
        />
      </div>
      {isOpen && <div className="collapsibleBody">{children}</div>}
    </div>
  );
};

const Serverpage = () => {
  const [profileImage, setProfileImage] = useState(null);
  const profileNickname = useSelector((state) => state.user.nickname)

  const [serverName, setServerName] = useState("기본 서버 이름");

  const [channels, setChannels] = useState([]);

  const [onlineMembers, setOnlineMembers] = useState([]);
  const [offlineMembers, setOfflineMembers] = useState([]);
  const profileImageURL = useSelector((state) => state.user.profile);
  
  const { seq } = useParams();

  useEffect(() => {
    // 만약 redux에서 프로필 이미지가 null이면 기본 이미지로 설정
    if (profileImageURL === null) {
      setProfileImage("/withView2.png");
    } else {
      setProfileImage(profileImageURL);
    }
  }, [profileImageURL]);


  useEffect(() => {
    fetchChannels();
    // Fetch online and offline members here...
  }, []);

  const fetchChannels = async () => {
    try {
      const response = await axios.get(
        `https://i9d208.p.ssafy.io/api/servers/${seq}/channels`
      );
      const data = response.data;
      setChannels(data.channels);
    } catch (error) {
      console.error("Error fetching channels:", error);
    }
  };

  const createChannel = () => {
    // Define your create channel function here
  };

  const settings = {
    dots: true,
    infinite: false,
    speed: 500,
    slidesToShow: 3,
    slidesToScroll: 3,
  };

  return (
    <div className="mainbox">
      <div className="innermain">
        <ServerOptions
          profileImage={profileImage}
          profileNickname={profileNickname}
        />
        <hr className="serverOptionsLine_main" />
        <div className="backAndServerNameContainer">
          <img
            className="backArrowIcon"
            src="/backarrow.png"
            alt="Go Back"
            onClick={() => window.history.back()}
          />
          <h1 className="channelServerNameText">{serverName}</h1>
        </div>
        <div className="channelNameText">채널 목록</div>
        <div className="sliderChannelContainer">
          <Slider {...settings}>
            <div>
              <CreateChannelCard onClick={createChannel} />
            </div>
            {channels.map((channel) => (
              <div key={channel.seq}>
                <ChannelCard channel={channel} />
              </div>
            ))}
          </Slider>
        </div>
        <div className="channelNameText">맴버 목록</div>
        <Collapsible title="온라인">
          <div className="membersContainer">
            {onlineMembers.map((member) => (
              <Member key={member.id} member={member} />
            ))}
          </div>
        </Collapsible>
        <Collapsible title="오프라인">
          <div className="membersContainer">
            {offlineMembers.map((member) => (
              <Member key={member.id} member={member} />
            ))}
          </div>
        </Collapsible>
      </div>
    </div>
  );
};

export default Serverpage;
