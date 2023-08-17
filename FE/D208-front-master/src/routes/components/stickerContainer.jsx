// firstmain.jsx
import { useState, useEffect, useRef } from "react";
import "../../css/stickerContainer.css";
import { useSelector } from "react-redux";
import axiosInstance from "../axiosinstance";
import yummy from "../../assets/imo/yummy.png";
import {
  faMagnifyingGlass,
  faSquarePlus,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import StickerRegistModal from "./stickerRegistModal";

const StickerContainer = (props) => {
  const [basicStickerList, setBasicStickerList] = useState([]);
  const [isOpen, setIsOpen] = useState(false);
  const [myStickerList, setMyStickerList] = useState([]);
  const userSeq = useSelector((state) => state.user.seq);
  const token = useSelector((state) => state.token);
  const keyword = useRef();
  const headers = {
    Authorization: `Bearer ${token}`,
  };
  const keyPressHandler = (e) => {
    if (e.code == "Enter") {
      setMyStickers();
      setBasicStickers();
    }
  };
  const isOpenSettings = () => {
    setIsOpen(!isOpen);
  };
  const setMyStickers = () => {
    let formData = new FormData();
    let params = {
      userSeq: userSeq,
      keyword: keyword.current.value,
    };
    let paramsString = new URLSearchParams(params);

    axiosInstance
      .get(
        `/${props.table}/users?` + paramsString.toString(),
        { headers },
        formData
      )
      .then((response) => {
        console.log("유저가 등록한 스티커");
        console.log(response);
        setMyStickerList(response.data.stickers);
      })
      .catch((err) => {
        console.log("내 스티커 로드를 실패했습니다.");
      });
  };
  const setBasicStickers = () => {
    let formData = new FormData();
    let params = {
      userSeq: 0,
      keyword: keyword.current.value,
    };

    let paramsString = new URLSearchParams(params);

    axiosInstance
      .get(
        `/${props.table}/users?` + paramsString.toString(),
        { headers },
        formData
      )
      .then((response) => {
        console.log("기본 스티커");
        console.log(response);
        setBasicStickerList(response.data.stickers);
      })
      .catch((err) => {
        console.log("스티커 로드를 실패했습니다.");
      });
  };

  useEffect(() => {
    setMyStickers();
    setBasicStickers();
  }, []);

  return (
    <div className="sticker-container">
      <div className="sticker-header">
        <div className="search-bar">
          <input
            ref={keyword}
            name="keyword"
            placeholder="검색어를 입력해주세요."
            onKeyUp={keyPressHandler}
          ></input>
          <FontAwesomeIcon
            className="s-icon"
            icon={faMagnifyingGlass}
            style={{ color: "#6AA4E8", margin: "7px" }}
            onClick={() => {
              setMyStickers();
              setBasicStickers();
            }}
          />
        </div>
        <button
          className="sticker-add-btn"
          type="button"
          onClick={isOpenSettings}
        >
          <FontAwesomeIcon
            className="add-icon"
            icon={faSquarePlus}
            style={{ color: "#ffffff", padding: "7px" }}
          />
        </button>
        <StickerRegistModal
          table={props.table}
          title={props.title}
          isOpen={isOpen}
          onChange={isOpenSettings}
          updateMyStickers={setMyStickers}
        ></StickerRegistModal>
      </div>
      <div className="sticker-list-container">
        <div className="sticker-list-title">
          <span className="user-bg">내 {props.title} 목록</span>
        </div>

        {myStickerList.map((sticker, index) => (
          <img
            key={index}
            id={sticker.originalName.substring(
              0,
              sticker.originalName.indexOf(".")
            )}
            className="sticker"
            src={
              `https://dm51j1y1p1ekp.cloudfront.net/${props.imgDirectory}/` +
              sticker.searchName
            }
            onClick={() => props.addFile(sticker.searchName)}
          ></img>
        ))}

        <div className="sticker-list-title">
          <span className="user-bg">기본 {props.title} 목록</span>
        </div>
        {basicStickerList.map((sticker, index) => (
          <img
            key={index}
            id={sticker.originalName.substring(
              0,
              sticker.originalName.indexOf(".")
            )}
            className="sticker"
            src={
              `https://dm51j1y1p1ekp.cloudfront.net/${props.imgDirectory}/` +
              sticker.searchName
            }
            onClick={() => props.addFile(sticker.searchName)}
          ></img>
        ))}
      </div>
    </div>
  );
};

export default StickerContainer;
