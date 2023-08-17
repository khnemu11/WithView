// firstmain.jsx
import { useState, useEffect, useRef } from "react";
import Modal from "react-modal";
import "../../css/presetLoadModal.css";
import { useSelector } from "react-redux";
import axiosInstance from "../axiosinstance";

const PresetLoadModal = (props) => {
  const [presetList, setPresetList] = useState([]);
  const [searchList, setSearchList] = useState([]);
  const userSeq = useSelector((state) => state.user.seq);
  const token = useSelector((state) => state.token);
  const headers = {
    Authorization: `Bearer ${token}`,
  };
  const onOpen = () => {
    axiosInstance
      .get(`/preset/${userSeq}/list`, { headers })
      .then((response) => {
        console.log(response);

        if (response.data.success == false) {
          return;
        }

        setPresetList(response.data.PresetListInfo);
        setSearchList(response.data.PresetListInfo);
      })
      .catch((err) => {
        console.log(err);
      });
  };
  const onClose = () => {};
  return (
    <Modal
      isOpen={props.isOpen}
      onRequestClose={() => {
        props.onChange();
      }}
      onAfterOpen={() => onOpen()}
      onAfterClose={() => onClose()}
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
          width: "50vw",
          height: "85vh",
          maxWidth: "1440px",
          maxHeight: "1200px",
          position: "relative",
          border: "1px solid #ccc",
          borderRadius: "20px",
          margin: "20px",
        },
      }}
    >
      <div className="modal-container">
        <div className="modal-title">프리셋 불러오기</div>
        <div className="modal-form">
          <div className="modal-input">
            <input
              className="modal-input-content"
              name="keyword"
              placeholder="검색어를 입력해주세요."
              onChange={(e) => {
                if (e.target.value == "") {
                  setSearchList(presetList);
                } else {
                  setSearchList(
                    presetList.filter((preset) => {
                      return preset.presetName.includes(e.target.value);
                    })
                  );
                }
              }}
            ></input>
          </div>
          <div className="preset-list">
            {searchList.map((preset, index) => (
              <div
                key={index}
                className="card preset-card"
                style={{ position: "relative" }}
                onClick={() => {
                  console.log("선택한 스테이지");
                  console.log(preset.stage);
                  props.loadCanvas(preset.stage);
                  props.onChange();
                }}
              >
                <div className="card-image preset-card-image">
                  <figure className="image">
                    <img
                      src={`https://dm51j1y1p1ekp.cloudfront.net/preset/${preset.presetImgSearchName}`}
                    />
                  </figure>
                </div>
                <div className="card-content">
                  <div className="media">
                    <div className="media-content">
                      <p className="title is-5 load-title">
                        {preset.presetName}
                      </p>
                      <div className="is-right">
                        <p className="subtile is-6">
                          {preset.registerTime == null
                            ? preset.registerTime
                            : preset.registerTime.substring(0, 10)}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </Modal>
  );
};

export default PresetLoadModal;
