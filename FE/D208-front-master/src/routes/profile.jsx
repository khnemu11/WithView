import React, { useState, useRef, useEffect } from "react";
import Cropper from "react-cropper";
import axios from "axios";
import "cropperjs/dist/cropper.css";
import "../css/mainpage.css"; // CSS 파일 임포트
import "../css/profile.css";
import "../css/firstmain.css";
import ServerOptions from "./components/serveroptions";
import { useSelector } from "react-redux";
import { clearToken } from "../redux/actions/tokenActions";
import { clearUser } from "../redux/actions/userActions";

const Profile = () => {
  const [view, setView] = useState("profile"); // view 상태를 선언하고 기본값을 'profile'로 설정합니다.
  const [profileMessage, setProfileMessage] =
    useState("이것은 상태 메세지입니다."); // 상태 메시지를 위한 상태를 생성합니다.
  const [tempProfileMessage, setTempProfileMessage] = useState("");
  const [tempProfileNickname, setTempProfileNickname] = useState("");
  const profileNickname = useSelector((state) => state.user.nickname)
  const [profilePassword, setProfilePassword] = useState("");
  const [profilePasswordCheck, setProfilePasswordCheck] = useState("");
  const [profileLeaveCheck, setProfileLeaveCheck] = useState("");
  const [profileImage, setProfileImage] = useState("/프사.png");
  const profileImageUrl = `https://dm51j1y1p1ekp.cloudfront.net/profile/${profileImage}`;

  const [isCropModalOpen, setIsCropModalOpen] = useState(false);
  const [imageToCrop, setImageToCrop] = useState(null);
  const cropperRef = useRef(null);
  const profileImageURL = useSelector((state) => state.user.profile);
  const url = "https://i9d208.p.ssafy.io/api";

  useEffect(() => {
    // 만약 redux에서 프로필 이미지가 null이면 기본 이미지로 설정
    if (profileImageURL === null) {
      setProfileImage("/withView2.png");
    } else {
      setProfileImage(profileImageURL);
    }
  }, [profileImageURL]);

  const checkLogout = (e) => {
    e.preventDefault()
    axios({
      method : 'POST',
      url : `${url}/login/logout`
    })
    .then((res) => {
      console.log(res.data)
      if(res.data.success){
        clearToken()
        clearUser()
      }
    })
    .catch((err)=>{
      console.log(err)
      alert('로그인 실패!')
    })
  } 
  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImageToCrop(reader.result);
        setIsCropModalOpen(true); // 모달 열기
      };
      reader.readAsDataURL(file);
    }
  };
  const handleCropImage = () => {
    const imageElement = cropperRef?.current;
    const cropper = imageElement?.cropper;
    setProfileImage(
      cropper
        .getCroppedCanvas({
          width: 300,
          height: 300,
        })
        .toDataURL()
    );
    setIsCropModalOpen(false); // 모달 닫기
  };

  const handleViewChange = (viewName) => {
    setView(viewName);
    resetInputs();
  };

  const handleConfirmMessageButtonClick = () => {
    // '확인' 버튼 클릭 시, 임시 상태를 실제 상태로 업데이트
    setProfileMessage(tempProfileMessage);
    setTempProfileMessage(""); // 필요하다면 임시 상태 초기화
  };

  const handleConfirmNicknameButtonClick = () => {
    // '확인' 버튼 클릭 시, 임시 상태를 실제 상태로 업데이트
    setProfileNickname(tempProfileNickname);
    setTempProfileMessage(""); // 필요하다면 임시 상태 초기화
  };

  const resetInputs = () => {
    setTempProfileMessage("");
    setTempProfileNickname("");
    setProfilePassword("");
    setProfilePasswordCheck("");
  };

  const renderSection = () => {
    switch (view) {
      case "profile":
        return (
          <div>
            <p className="profile-profile">프로필</p>
            <p className="profile-text">새 프로필 사진을 올려주세요!</p>
            <label htmlFor="imageUpload">
              <img
                src="/uploadimage.png"
                className="image-upload"
                alt="Upload"
              />
            </label>
            <input
              id="imageUpload"
              type="file"
              accept="image/*"
              onChange={handleImageUpload}
              style={{ display: "none" }}
            />
            <p className="profile-text">
              새 프로필 상태 메시지를 입력해주세요 !
            </p>
            <div className="input-div">
              <input
                className="profile-messageInput"
                type="text"
                value={tempProfileMessage}
                onChange={(e) => setTempProfileMessage(e.target.value)}
              />
            </div>
            <div className="button-div">
              <button
                className="button mt-2 has-text-white profile-ok-button"
                onClick={handleConfirmMessageButtonClick}
              >
                확인
              </button>
            </div>
          </div>
        );
      case "nickname":
        return (
          <div>
            <p className="profile-nickname">닉네임 변경</p>
            <p className="profile-text">새 닉네임을 입력해주세요 !</p>
            <div className="input-div">
              <input
                className="profile-messageInput"
                type="text"
                value={tempProfileNickname}
                onChange={(e) => setTempProfileNickname(e.target.value)}
              />
            </div>
            <div className="button-div">
              <button
                className="button mt-2 has-text-white profile-ok-button"
                onClick={handleConfirmNicknameButtonClick}
              >
                확인
              </button>
            </div>
          </div>
        );
      case "password":
        return (
          <div>
            <p className="profile-password">비밀번호 변경</p>
            <p className="profile-text">새 비밀번호를 입력해주세요 !</p>
            <div className="input-div">
              <input
                className="profile-messageInput"
                type="text"
                value={profilePassword}
                onChange={(e) => setProfilePassword(e.target.value)}
              />
            </div>
            <p className="profile-text">다시 한 번 입력해주세요 !</p>
            <div className="input-div">
              <input
                className="profile-messageInput"
                type="text"
                value={profilePasswordCheck}
                onChange={(e) => setProfilePasswordCheck(e.target.value)}
              />
            </div>
            <br></br>
            <div className="button-div">
              <button className="button mt-2 has-text-white profile-ok-button">
                확인
              </button>
            </div>
          </div>
        );
      case "leave":
        return (
          <div>
            <p className="profile-leave">회원탈퇴</p>
            <p className="profile-text">정말로 회원탈퇴를 하실 건가요 ?</p>
            <br></br>
            <p className="profile-text">
              되돌릴 수 없으니 신중하게 확인하세요 !
            </p>
            <p className="profile-minitext">
              회원탈퇴를 하기 위해서 비밀번호를 다시 입력해주세요 !
            </p>
            <div className="input-div">
              <input
                className="profile-messageInputLeave"
                type="text"
                value={profileLeaveCheck}
                onChange={(e) => setProfileLeaveCheck(e.target.value)}
              />
            </div>
            <div className="button-div">
              <button className="button mt-2 has-text-white profile-leave-button">
                확인
              </button>
            </div>
          </div>
        );
      default:
        return <p>프로필 정보</p>;
    }
  };

  return (
    <div className="mainbox">
      <div className="innermain">
        <ServerOptions
          profileImage={profileImageUrl}
          profileNickname={profileNickname}
        />

        <hr className="serverOptionsLine_profile" />
        <div className="columns profileBox">
          <div className="column is-3 column-left is-flex is-flex-direction-column">
            <img
              src="/withView.png"
              className="left-column-image"
              alt="With View"
            />
            <div className="user-info mt-2">
              <p className="title is-4 pl-3">회원정보 수정</p>
              <p
                className="subtitle is-6 clickable-subtitle"
                onClick={() => handleViewChange("profile")}
              >
                프로필
              </p>
              <p
                className="subtitle is-6 pl-3 clickable-subtitle"
                onClick={() => handleViewChange("nickname")}
              >
                닉네임
              </p>
              <p
                className="subtitle is-6 pl-3 clickable-subtitle"
                onClick={() => handleViewChange("password")}
              >
                비밀번호
              </p>
              <p
                className="subtitle is-6 pl-3 clickable-subtitle"
                onClick={() => handleViewChange("leave")}
              >
                회원탈퇴
              </p>
            </div>
            <button 
              className="button mt-4 has-text-white logout-button"
              onClick={checkLogout}
            >
              로그아웃
            </button>
          </div>
          <div className="column is-6">{renderSection()}</div>
          <div className="column is-3">
            <div className="is-flex is-flex-direction-column is-align-items-center">
              <img
                src={profileImageUrl}
                className="profile-side-image"
                alt="Profile"
              />
              <p className="profile-side-name">{profileNickname}</p>
              <p className="profile-side-message">{profileMessage}</p>
            </div>
          </div>
        </div>
      </div>
      {isCropModalOpen && (
        <div className="modal is-active">
          <div className="modal-background"></div>
          <div className="modal-content-profile-image">
            <Cropper
              ref={cropperRef}
              src={imageToCrop}
              style={{ width: 600, height: 400 }}
              aspectRatio={1} // 1:1 비율로 원 형태를 만듭니다.
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
              ready={() => {
                const imageElement = cropperRef?.current;
                const cropper = imageElement?.cropper;
                cropper.zoomTo(0.5); // 50% 축소
              }}
            />
            <button
              onClick={handleCropImage}
              className="image-apply-button has-text-white"
            >
              적용하기
            </button>
          </div>
          <button
            onClick={() => setIsCropModalOpen(false)}
            className="modal-close is-large"
            aria-label="close"
          ></button>
        </div>
      )}
    </div>
  );
};

export default Profile;
