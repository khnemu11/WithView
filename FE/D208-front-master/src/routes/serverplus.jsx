import React, { useState, useRef, useEffect } from "react";
import "../css/mainpage.css"; // CSS 파일 임포트
import "../css/firstmain.css";
import "../css/serverplus.css";
import Cropper from "react-cropper";
import "cropperjs/dist/cropper.css";
import ServerOptions from "./components/serveroptions";
import { useSelector } from "react-redux";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import axiosInstance from "./axiosinstance";



const ServerPlus = () => {
  const profileNickname = useSelector((state) => state.user.nickname);
  const [profileImage, setProfileImage] = useState("null");
  const [serverImage, setServerImage] = useState("/withView.png");
  const [editedImage, setEditedImage] = useState(null);
  const [editedImageShow, setEditedImageShow] = useState(null);
  const [serverName, setServerName] = useState("");
  const hostSeqRef = useRef(useSelector((state) => state.user.seq));
  const [isCropModalOpen, setIsCropModalOpen] = useState(false);
  const [imageToCrop, setImageToCrop] = useState(null);
  const cropperRef = useRef(null);
  const profileImageURL = useSelector((state) => state.user.profile);
  const profileImageUrl = `https://dm51j1y1p1ekp.cloudfront.net/profile/${profileImageURL}`;
  const token = useSelector((state) => state.token);



  const navigate = useNavigate();

  useEffect(() => {
    // 만약 redux에서 프로필 이미지가 null이면 기본 이미지로 설정
    if (profileImageURL === null) {
      setProfileImage("/withView2.png");
    } else {
      setProfileImage(profileImageUrl);
    }
  }, [profileImageURL]);

  // Base64 -> Blob
  function base64ToBlob(base64String) {
    const byteString = atob(base64String.split(",")[1]);
    const arrayBuffer = new ArrayBuffer(byteString.length);
    const uint8Array = new Uint8Array(arrayBuffer);

    for (let i = 0; i < byteString.length; i++) {
      uint8Array[i] = byteString.charCodeAt(i);
    }

    return new Blob([uint8Array], { type: "image/png" });
  }

  const handleServerCreateButtonClick = () => {
    const formData = new FormData();
    formData.append("hostSeq", hostSeqRef.current);
    formData.append("name", serverName);
    formData.append("file", editedImage);

    // axiosInstance를 사용하므로 기본 URL은 생략 가능
    axiosInstance
      .post("/servers", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
          "Authorization": `Bearer ${token}`
        },
      })
      .then((response) => {
        // 서버 생성 성공 처리
        console.log(editedImage);
        console.log("서버 생성 성공:", response.data);
        // ... (추가적인 처리 로직)
        const serverSeq = response.data.server.seq;
        navigate(`/server/${serverSeq}`);
      })
      .catch((error) => {
        // 서버 생성 실패 처리
        console.error("서버 생성 실패:", error);
        // ... (에러 처리 로직)
      });
  };


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
    const croppedCanvas = cropper.getCroppedCanvas({
      width: 300,
      height: 300,
    });

    // 캔버스에서 Base64로 변환된 PNG 이미지 가져오기
    const croppedImageDataURL = croppedCanvas.toDataURL("image/png");

    // Base64를 Blob 객체로 변환
    const blob = base64ToBlob(croppedImageDataURL);

    // Blob 객체를 File 객체로 변환 (파일 이름은 "croppedSeverImage.png"로 설정)
    const file = new File([blob], "croppedSeverImage.png", {
      type: "image/png",
    });

    // File 객체를 editedImage에 설정
    setEditedImage(file);
    setEditedImageShow(croppedImageDataURL);

    setIsCropModalOpen(false); // 모달 닫기
  };

  return (
    <div className="mainbox">
      <div className="innermain">
        <ServerOptions
          profileImage={profileImage}
          profileNickname={profileNickname}
        />

        <hr className="serverOptionsLine_profile" />

        <div className="serverCreate_text">서버 만들기</div>
        <label htmlFor="imageUpload-server">
          <img
            src={editedImageShow || "/uploadimage.png"}
            className="image-upload-server"
            alt="Upload"
          />
        </label>
        <input
          id="imageUpload-server"
          type="file"
          accept="image/*"
          onChange={handleImageUpload}
          style={{ display: "none" }}
        />
        <div className="input-div">
          <input
            className="serverCreateInput"
            type="text"
            placeholder="서버 이름"
            value={serverName}
            onChange={(e) => setServerName(e.target.value)}
            style={{ paddingLeft: "15px" }}
          />
        </div>
        <div className="button-div">
          <button
            className="button mt-2 has-text-white serverplus-ok-button"
            onClick={() =>
              handleServerCreateButtonClick(hostSeqRef, serverName, editedImage)
            }
          >
            생성하기
          </button>
        </div>
        {isCropModalOpen && (
          <div className="modal is-active">
            <div className="modal-background-serverplus"></div>
            <div className="modal-content-serverplus-image">
              <Cropper
                ref={cropperRef}
                src={imageToCrop}
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
    </div>
  );
};

export default ServerPlus;
