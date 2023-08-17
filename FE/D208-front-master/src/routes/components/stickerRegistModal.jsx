// firstmain.jsx
import { useState, useEffect, useRef } from "react";
import Modal from "react-modal";
import "../../css/stickerRegistModal.css";
import { useSelector } from "react-redux";
import axiosInstance from "../axiosinstance";
import x from "../../assets/x.png";

const StickerRegistModal = (props) => {
  const userSeq = useSelector((state) => state.user.seq);
  const token = useSelector((state) => state.token);
  const [imgSrc, setImgSrc] = useState("");
  const [file, setFile] = useState(null);
  const [filename, setFileName] = useState("");
  const [name, setName] = useState("");
  const writer = useSelector((state) => state.user.nickname);

  const headers = {
    Authorization: `Bearer ${token}`,
  };
  const onOpen = () => {
    console.log("모달 열림");
  };
  const onClose = () => {};
  const insertSticker = (props) => {
    let formData = new FormData();
    console.log(file);

    formData.append("file", file);
    formData.append("userSeq", userSeq);
    formData.append("writer", writer);
    formData.append("name", name);

    axiosInstance
      .post(`/${props.table}`, formData, { headers })
      .then((response) => {
        alert(`${props.title}가 성공적으로 저장되었습니다.`);
        props.updateMyStickers();
        console.log(response);
      })
      .catch((err) => {
        alert(`${props.title} 저장을 실패했습니다.`);
        console.log(err);
      });
  };
  const preview = (e) => {
    const reader = new FileReader();
    reader.readAsDataURL(e.target.files[0]);
    setFileName(e.target.files[0].name);
    setFile(e.target.files[0]);
    reader.onload = () => {
      setImgSrc(reader.result);
    };
  };

  const nameSetting = (e) => {
    setName(e.target.value);
  };
  return (
    <Modal
      isOpen={props.isOpen}
      onRequestClose={() => {
        props.onChange();
      }}
      style={{
        overlay: {
          position: "fixed",
          zIndex: 1020,
          top: 0,
          left: 0,
          width: "100vw",
          height: "100vh",
          background: "rgba(0, 0, 0, 0.5)",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        },
        content: {
          background: "white",
          width: "55%",
          height: "80vh",
          maxWidth: "1440px",
          maxHeight: "1240px",
          overflowY: "auto",
          position: "relative",
          border: "1px solid #ccc",
          borderRadius: "20px",
          margin: "20px",
        },
      }}
    >
      <div className="regist-container">
        <div className="modal-title">{props.title} 등록하기</div>
        <div className="modal-form regist-form">
          <div className="modal-input">
            <div className="modal-input-title sticker-input-title">
              {props.title} 이름
            </div>
            <input
              className="modal-sticker-name"
              onChange={(e) => {
                setName(e.target.value);
              }}
            ></input>
          </div>
          <div className="modal-input">
            <div className={"modal-input-title"}>{props.title} 미리보기</div>
            <div className="image-preview">
              <label htmlFor="imageUpload">
                <img
                  src={imgSrc != "" ? imgSrc : "/uploadimage.png"}
                  className="sticker-upload"
                  alt="Upload"
                />
              </label>
              <input
                id="imageUpload"
                className="modal-input-content"
                name="file"
                type="file"
                onChange={preview}
                accept="image/*"
                style={{ display: "none" }}
              ></input>
            </div>
          </div>
          <div className="w-100 flex">
            <button
              type="button"
              className="model-input-btn"
              onClick={() => {
                insertSticker(props);
                props.onChange();
              }}
            >
              등록하기
            </button>
          </div>
        </div>
      </div>
    </Modal>
  );
};

export default StickerRegistModal;
