import React, { useState, useEffect, useRef } from "react";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import "../css/firstmain.css";
import "../css/mainpage.css"; // CSS 파일 임포트
import "../css/serverpage.css";
import ServerOptions from "./components/serveroptions";
import axios from "axios";
import { useSelector } from "react-redux";
import { useParams, useNavigate } from "react-router-dom";
import Modal from "react-modal";
import Cropper from "react-cropper";
import "cropperjs/dist/cropper.css";
import Popover from "react-popover";
import axiosInstance from "./axiosinstance";

Modal.setAppElement("#root");

const Serverpage = () => {
  const [profileImage, setProfileImage] = useState(null);
  const profileNickname = useSelector((state) => state.user.nickname);

  const [serverName, setServerName] = useState("기본 서버 이름");

  const [channels, setChannels] = useState([]);

  const [serverMembers, setServerMembers] = useState([]);
  const profileImageURL = useSelector((state) => state.user.profile);
  const profileImageUrl = `https://dm51j1y1p1ekp.cloudfront.net/profile/${profileImageURL}`;

  const userSeq = useSelector((state) => state.user.seq);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [channelName, setChannelName] = useState("");
  const [croppedImage, setCroppedImage] = useState(null);
  const cropperRef = useRef(null);
  const [isHost, setIsHost] = useState(false);

  const { seq } = useParams();
  const token = useSelector((state) => state.token);

  const navigate = useNavigate();

  const [editingChannel, setEditingChannel] = useState(null);

  const [inviteLinkModalOpen, setInviteLinkModalOpen] = useState(false);
  const [inviteLink, setInviteLink] = useState("");

  //서버 이름
  useEffect(() => {
    const fetchServerName = async () => {
      try {
        const response = await axiosInstance.get(`/servers/${seq}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        const serverInfo = response.data.server;

        setServerName(serverInfo.name);
        // 사용자가 호스트인지 확인
        setIsHost(serverInfo.hostSeq === userSeq);
      } catch (error) {
        console.error("Error fetching server name:", error);
      }
    };
    fetchServerName();
  }, [seq]);

  const MemberPopover = ({ member }) => {
    const [isPopoverOpen, setIsPopoverOpen] = useState(false);
    const profileImageUrl = `https://dm51j1y1p1ekp.cloudfront.net/profile/${member.profileImgSearchName}`;
    const [showFriendAddPopover, setShowFriendAddPopover] = useState(false);
    const profileImageRef = useRef(null);

    const togglePopover = (e) => {
      setIsPopoverOpen((prev) => !prev);
      e.stopPropagation(); // 이 부분 추가: 버블링 방지
      setIsPopoverOpen(!isPopoverOpen);
      if (isPopoverOpen) {
        setShowFriendAddPopover(false);
      }
    };

    const addFriend = async (e) => {
      e.stopPropagation(); // 버블링 중단

      const formData = new FormData();
      formData.append("followingUserSeq", userSeq);
      formData.append("followiedUserSeq", member.seq);

      try {
        await axiosInstance.post("/friends", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${token}`,
          },
        });
        alert("친구추가 되었습니다!");
        console.log(member);
        console.log(member.seq);
      } catch (error) {
        console.error("Error adding friend:", error);
      }

      setShowFriendAddPopover(false); // 팝오버 닫기
    };

    const popoverBody = (
      <div className="profilePopover">
        <img
          src={profileImageUrl}
          alt="profile"
          ref={profileImageRef}
          onClick={(e) => {
            e.stopPropagation(); // 이 부분 추가: 버블링 방지
            setShowFriendAddPopover((prev) => !prev);
          }}
          onError={(e) => {
            e.target.onerror = null;
            e.target.src = "/withView2.png";
          }}
        />
        {showFriendAddPopover && (
          <div
            className="addFriendPopover"
            style={{
              position: "absolute",
              top: profileImageRef.current
                ? profileImageRef.current.offsetTop
                : 0,
              left: profileImageRef.current
                ? profileImageRef.current.offsetLeft
                : 0,
              width: profileImageRef.current
                ? profileImageRef.current.offsetWidth
                : "100%",
              height: profileImageRef.current
                ? profileImageRef.current.offsetHeight
                : "100%",
              backgroundColor: "#ffffff7a",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              cursor: "pointer",
              fontWeight: "bold",
              fontSize: "large",
            }}
            onClick={addFriend}
          >
            친구 추가
          </div>
        )}
        <h2>{member.nickname}</h2>
        <p>{member.profileMsg}</p>
      </div>
    );

    return (
      <div onClick={togglePopover}>
        <Popover
          isOpen={isPopoverOpen}
          body={popoverBody}
          preferPlace="above"
          onOuterAction={togglePopover}
        >
          <img
            src={profileImageUrl}
            alt="profile"
            onError={(e) => {
              e.target.onerror = null;
              e.target.src = "/withView2.png";
            }}
          />
        </Popover>
      </div>
    );
  };

  // 서버 맴버 목록
  useEffect(() => {
    const fetchMembers = async () => {
      try {
        const response = await axiosInstance.get(`/servers/${seq}/users`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (response.data.success) {
          setServerMembers(response.data.users);
        } else {
          console.error("Failed to fetch members data");
        }
      } catch (error) {
        console.error("Error fetching members data:", error);
      }
    };

    fetchMembers();
  }, [seq]); // seq 값이 변경될 때마다 새로운 요청을 보내도록 설정

  const ChannelCard = ({ channel, isHost }) => {
    const [isPopoverOpen, setIsPopoverOpen] = useState(false);
    const togglePopover = () => {
      setIsPopoverOpen(!isPopoverOpen);
    };

    const popoverBody = (
      <div
        style={{
          backgroundColor: "white",
          borderRadius: "10px",
          padding: "10px",
        }}
      >
        <button
          style={{ display: "block", marginBottom: "10px" }}
          onClick={() => handleEditChannelClick(channel)}
        >
          채널 수정
        </button>
        <button
          style={{ display: "block" }}
          onClick={() => {
            /* 채널 삭제 로직 */
          }}
        >
          채널 삭제
        </button>
      </div>
    );

    const joinChannel = (e) => {
      e.stopPropagation(); // 이 부분 추가: 버블링 방지
      // 그룹채팅 화면으로 이동하는 경로를 설정합니다.
      const groupchatLinkPath = `/groupchat`;
      // 클릭 시 그룹채팅로 이동합니다.
      console.log(channel);
      console.log(channel.seq);
      navigate(groupchatLinkPath, {
        state: {
          serverSeq: seq,
          channelSeq: channel.seq,
          channelName: channel.name,
        },
      });
    };

    return (
      <div className="channelCard">
        <div className="channelCard-imageContainer" onClick={joinChannel}>
          <img
            src={`https://dm51j1y1p1ekp.cloudfront.net/channel-background/${channel.backgroundImgSearchName}`}
            alt={channel.name}
            className="channelCard-image"
          />
        </div>
        <div className="channelCard-content">
          <p className="channelCard-title">{channel.name}</p>
          {isHost && (
            <div>
              <Popover
                isOpen={isPopoverOpen}
                body={popoverBody}
                place="above"
                tipSize={0.01} // 작은 팁 사이즈로 팝오버가 버튼 위에 위치하도록 합니다
                onOuterAction={togglePopover}
              >
                <img
                  src="/dots-three-vertical.png"
                  alt="Menu"
                  className="channelCard-menuIcon"
                  onClick={togglePopover}
                />
              </Popover>
            </div>
          )}
        </div>
      </div>
    );
  };

  const handleImageChange = (event) => {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = () => {
      setCroppedImage(reader.result);
    };

    reader.readAsDataURL(file);
  };

  useEffect(() => {
    // 만약 redux에서 프로필 이미지가 null이면 기본 이미지로 설정
    if (profileImageURL === null) {
      setProfileImage("/withView2.png");
    } else {
      setProfileImage(profileImageUrl);
    }
  }, [profileImageURL]);

  useEffect(() => {
    fetchChannels();
    // Fetch online and offline members here...
  }, []);

  const fetchChannels = async () => {
    try {
      const response = await axiosInstance.get(`/servers/${seq}/channels`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      const data = response.data;
      setChannels(data.channels);
    } catch (error) {
      console.error("Error fetching channels:", error);
    }
  };

  const createChannel = async () => {
    const cropper = cropperRef.current.cropper;
    const croppedCanvas = cropper.getCroppedCanvas();

    croppedCanvas.toBlob(async (blob) => {
      const formData = new FormData();
      const file = new File([blob], "newFile.png", { type: blob.type });

      formData.append("file", file);
      formData.append("name", channelName);

      for (let entry of formData.entries()) {
        console.log(entry);
      }

      try {
        const response = await axiosInstance.post(
          `/servers/${seq}/channels`,
          formData,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "multipart/form-data",
            },
          }
        );

        if (response.status === 200) {
          // 채널 생성 후 바로 채널 목록을 다시 불러옵니다.
          await fetchChannels();
          setIsModalOpen(false);
          resetModal();
        }
      } catch (error) {
        console.error("Error creating channel:", error);
      }
    });
  };

  const handleServerMenuClick = () => {
    // TODO: 여기에 서버 수정 및 삭제 메뉴를 띄우는 로직을 추가합니다.
    // 서버 수정과 삭제에 필요한 서버 정보는 serverInfo에 저장되어 있습니다.
    navigate(`/server/${seq}/edit`, { state: { isHost } });
  };

  const settings = {
    dots: true,
    infinite: false,
    speed: 500,
    slidesToShow: 3,
    slidesToScroll: 3,
  };
  //모달 리셋
  const resetModal = () => {
    setChannelName("");
    setCroppedImage(null);
    setIsModalOpen(false);
    setEditingChannel(null);
  };

  //채널 수정' 버튼이 눌렸을 때 setEditingChannel를 사용해 수정하려는 채널의 정보를 설정
  const handleEditChannelClick = (channel) => {
    setChannelName(channel.name);
    setCroppedImage(null);
    setEditingChannel(channel);
    setIsModalOpen(true);
  };

  const editChannel = async () => {
    const cropper = cropperRef.current.cropper;
    const croppedCanvas = cropper.getCroppedCanvas();

    croppedCanvas.toBlob(async (blob) => {
      const formData = new FormData();
      const file = new File([blob], "newFile.png", { type: blob.type });

      formData.append("file", file);
      formData.append("name", channelName);

      for (let entry of formData.entries()) {
        console.log(entry);
      }

      try {
        const response = await axiosInstance.post(
          `/servers/${seq}/channels/${editingChannel.seq}`,
          formData,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "multipart/form-data",
            },
          }
        );

        if (response.status === 200) {
          await fetchChannels();
          setIsModalOpen(false);
          setEditingChannel(null);
          resetModal();
        }
      } catch (error) {
        console.error("Error editing channel:", error);
      }
    });
  };

  const copyToClipboard = (text) => {
    navigator.clipboard
      .writeText(text)
      .then(() => {
        console.log("Text copied to clipboard");
      })
      .catch((err) => {
        console.error("Could not copy text: ", err);
      });
  };

  // 서버 초대 API
  const createInviteLink = async () => {
    try {
      const formData = new FormData();
      formData.append("userSeq", userSeq);
      formData.append("serverSeq", seq);

      const response = await axiosInstance.post("/invite", formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "multipart/form-data",
        },
      });

      if (response.status === 200) {
        const inviteLink = response.data.link;
        setInviteLink(inviteLink);
        copyToClipboard(inviteLink);
        setInviteLinkModalOpen(true);
      }
    } catch (error) {
      console.error("Error creating invite link:", error);
    }
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
            onClick={() => navigate("/mainpage")}
          />
          <h1 className="channelServerNameText">{serverName}</h1>
          {/* 서버 호스트일 경우에만 수정 및 삭제 메뉴 표시 */}
          {isHost && (
            <img
              className="serverMenuIcon"
              src="/serverEdit.png"
              alt="Menu"
              onClick={handleServerMenuClick}
            />
          )}
        </div>
        <div className="channelNameText">
          채널 목록
          <img
            className="plusIcon"
            src="/plus.png"
            alt="Create Channel"
            onClick={() => setIsModalOpen(true)}
          />
        </div>
        <Modal
          isOpen={isModalOpen}
          onRequestClose={resetModal}
          className="channelPlusModal"
          overlayClassName="cPMOverlay"
        >
          <button onClick={resetModal} className="closeModalButton">
            <img src="/backarrow.png" alt="Close modal" />
          </button>
          <div className="modal-content-channelplus-image">
            {croppedImage ? (
              <Cropper
                ref={cropperRef}
                src={croppedImage}
                style={{ width: 600, height: 400 }}
                viewMode={3}
                dragMode="move"
                guides={false}
                scalable={true}
                cropBoxMovable={true}
                cropBoxResizable={true}
                highlight={false} // 잘릴 영역 외에 부분을 어둡게 표시하려면 false로 설정합니다.
                background={true} // 크로퍼의 배경을 불투명하게 설정합니다.
                autoCropArea={1} // 잘릴 영역의 사이즈를 최대로 설정합니다.
                responsive={false}
                checkOrientation={false} // 이미지가 회전되는 것을 방지합니다.
              />
            ) : (
              <div className="cPImagePlaceholder">
                <img
                  src="/uploadimage.png"
                  alt="Upload Image"
                  onClick={() => document.getElementById("fileInput").click()}
                />
              </div>
            )}
            <input
              id="fileInput"
              type="file"
              onChange={handleImageChange}
              style={{ display: "none" }}
            />
            <div
              className="changeImageButton"
              onClick={() => document.getElementById("fileInput").click()}
            >
              이미지 변경
            </div>
            <input
              type="text"
              value={channelName}
              onChange={(e) => setChannelName(e.target.value)}
              placeholder="채널 이름"
              className="channelNameInput"
            />
            <button
              className="channel-apply-button has-text-white"
              onClick={editingChannel ? editChannel : createChannel}
            >
              {editingChannel ? "수정" : "만들기"}
            </button>
          </div>
        </Modal>
        <div className="sliderChannelContainer">
          <Slider {...settings}>
            {channels.map((channel) => (
              <div key={channel.seq}>
                <ChannelCard channel={channel} isHost={isHost} />
              </div>
            ))}
          </Slider>
        </div>
        <div className="channelNameText">
          맴버 목록
          <img
            className="plusIcon"
            src="/plus.png"
            alt="Create Channel"
            onClick={createInviteLink}
          />
        </div>
        {/* 초대 링크 생성 후 보여주는 모달 */}
        <Modal
          isOpen={inviteLinkModalOpen}
          onRequestClose={() => setInviteLinkModalOpen(false)}
          className="inviteLinkModal"
          overlayClassName="inviteLinkModalOverlay"
        >
          <p>초대 주소가 생성되었습니다 !</p>
          <p> 컨트롤 V로 링크를 전달하세요 !</p>
          <button onClick={() => setInviteLinkModalOpen(false)}>닫기</button>
        </Modal>
        <div className="scrollable-area-members">
          <div className="serverMembers">
            {serverMembers.map((member) => (
              <MemberPopover
                key={member.id}
                member={member}
                token={token}
                userSeq={userSeq}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Serverpage;
