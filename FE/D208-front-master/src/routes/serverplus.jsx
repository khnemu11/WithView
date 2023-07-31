import React, { useState, useRef } from "react";
import "../css/mainpage.css"; // CSS 파일 임포트
import "../css/firstmain.css";
import "../css/serverplus.css";
import Cropper from "react-cropper";
import "cropperjs/dist/cropper.css";
import ServerOptions from "./components/serveroptions";

const ServerPlus = () => {
  const [profileNickname, setProfileNickname] = useState("기본 닉네임");
  const [profileImage, setProfileImage] = useState("/프사.png");
  const [serverImage, setServerImage] = useState("/withView.png");
  const [serverName, setServerName] = useState("");

  const [isCropModalOpen, setIsCropModalOpen] = useState(false);
  const [imageToCrop, setImageToCrop] = useState(null);
  const cropperRef = useRef(null);

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
  const handleServerCreateButtonClick = () => {};

  const handleCropImage = () => {
    const imageElement = cropperRef?.current;
    const cropper = imageElement?.cropper;
    setServerImage(
      cropper
        .getCroppedCanvas({
          width: 300,
          height: 300,
        })
        .toDataURL()
    );
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
            src="/uploadimage.png"
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
          />
        </div>
        <div className="button-div">
          <button
            className="button mt-2 has-text-white serverplus-ok-button"
            onClick={handleServerCreateButtonClick}
          >
            확인
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
