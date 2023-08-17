// firstmain.jsx
import { useState, useEffect, useRef } from "react";
import Modal from "react-modal";
import "../../css/presetRegisterModal.css";
import x from "../../assets/x.png";
import { useSelector } from "react-redux";
import axiosInstance from "../axiosinstance";

const PresetRegistModal = (props) => {
  const [presetName, setPresetName] = useState("");
  const [imgUrl, setImgUrl] = useState("");
  const loaderRef = useRef();
  const userSeq = useSelector((state) => state.user.seq);
  const token = useSelector((state) => state.token);
  const headers = {
    Authorization: `Bearer ${token}`,
  };

  const onChange = (e) => {
    setPresetName(e.target.value);
  };

  const onOpen = (stage) => {
    if (stage == null) {
      return;
    }
    stage.children[1].hide();
    setImgUrl(stage.toDataURL());
  };
  const enterKey = () => {
    console.log(window.event.keyCode);
  };
  const onClose = (stage) => {
    if (stage == null) {
      return;
    }
    setImgUrl("");
    stage.children[1].show();
  };

  const insertPreset = (stage) => {
    console.log("등록하기 입력!");

    if (stage == null) {
      console.log("등록할 캔버스가 없습니다.");
      return;
    }
    console.log("이미지 url");
    console.log(imgUrl);

    // html2canvas(document.querySelector("#channel-screen"),{useCORS:true,allowTaint : false}).then(function(canvas) {
    //   console.log("이미지 변환!");
    //   setImgUrl(canvas.toDataURL());
    // });

    var binaryData = atob(imgUrl.split(",")[1]);
    var array = [];

    for (var i = 0; i < binaryData.length; i++) {
      array.push(binaryData.charCodeAt(i));
    }

    console.log(name);

    let filename = name;

    let file = new File([new Uint8Array(array)], filename + ".png", {
      type: "image/png",
    });

    console.log(file);

    document.querySelector("#remote-container").innerHTML = "";

    let formData = new FormData();

    formData.append("file", file);
    formData.append("presetName", presetName);
    formData.append("stage", stage.toJSON());
    formData.append("userSeq", userSeq);

    console.log("보냄!");

    axiosInstance
      .post(`/preset`, formData, { headers })
      .then((response) => {
        alert("프리셋이 성공적으로 저장되었습니다.");
        console.log(response);
      })
      .catch((err) => {
        alert("프리셋 저장을 실패했습니다.");
        console.log(err);
      });
  };
  return (
    <Modal
      isOpen={props.isOpen}
      onRequestClose={() => {
        props.onChange();
      }}
      onAfterOpen={() => onOpen(props.stage)}
      onAfterClose={() => onClose(props.stage)}
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
          height: "90%",
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
      <div className="modal-container regist-container">
        <div className="modal-title">프리셋 등록하기</div>
        <div className="modal-form regist-form">
          <div className="modal-input">
            <div className="modal-input-title">프리셋 이름</div>
            <input
              className="modal-input-content"
              name="presetName"
              onKeyUp={enterKey()}
              onChange={onChange}
              value={presetName}
            ></input>
          </div>
          <div className="modal-input">
            <div className={"modal-input-title"}>프리셋 미리보기</div>
            <span
              ref={loaderRef}
              className={
                "loader modal-loaders" + imgUrl == "" ? "show" : "hide"
              }
            ></span>
            <img
              className={"modal-input-image" + imgUrl == "" ? "hide" : "show"}
              src={imgUrl == "" ? x : imgUrl}
            ></img>
          </div>
          <div className="w-100 flex">
            <button
              type="button"
              className="model-input-btn"
              onClick={() => {
                insertPreset(props.stage);
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

export default PresetRegistModal;
