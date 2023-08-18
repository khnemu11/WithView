import React from "react";
import ServerOptions from "./components/serveroptions";
import { useSelector } from "react-redux";
import { useEffect, useState } from "react";
import "../css/board.css";
import axiosInstance from "./axiosinstance";
import { useNavigate } from "react-router";

function Board() {
  const profileNickname = useSelector((state) => state.user.nickname);
  const userPk = useSelector((state) => state.user.seq);
  const token = useSelector((state) => state.token);
  const [profileImage, setProfileImage] = useState(null);

  const [isModalActive, setIsModalActive] = useState(false);
  const profileImageURL = useSelector((state) => state.user.profile);
  const profileImageUrl = `https://dm51j1y1p1ekp.cloudfront.net/profile/${profileImageURL}`;
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [presetList, setPresetList] = useState([]);
  const [presetCard, setPresetCard] = useState([]);
  const [searchCard, setSearchCard] = useState([]);
  const [selectedImageId, setSelectedImageId] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    // 만약 redux에서 프로필 이미지가 null이면 기본 이미지로 설정
    if (profileImageURL === null) {
      setProfileImage("/withView2.png");
    } else {
      setProfileImage(profileImageUrl);
    }
  }, [profileImageURL]);

  useEffect(() => {
    axiosInstance({
      headers: {
        Authorization: `Bearer ${token}`,
      },
      method: "GET",
      url: `/board`,
    })
      .then((res) => {
        console.log(res.data);
        setPresetCard(res.data.BoardListInfo);
        setSearchCard(res.data.BoardListInfo);
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);

  useEffect(() => {
    axiosInstance({
      headers: {
        Authorization: `Bearer ${token}`,
      },
      method: "GET",
      url: `/preset/${userPk}/list`,
    })
      .then((res) => {
        console.log(res.data.PresetListInfo);
        setPresetList(res.data.PresetListInfo);
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);

  function WritePost() {
    console.log(selectedImageId[0]);

    if (!selectedImageId[0]) {
      alert("프리셋을 선택해주세요!");
    } else if (title === "") {
      alert("제목을 입력해주세요!");
    } else {
      axiosInstance({
        headers: {
          Authorization: `Bearer ${token}`,
        },
        method: "POST",
        url: `/board`,
        data: {
          userSeq: userPk,
          title: title,
          content: content,
          presetId: selectedImageId[0],
        },
      })
        .then((res) => {
          console.log(res.data);
          setTitle("");
          setContent("");
          setSelectedImageId([]);
          setIsModalActive(false);
          alert("작성 완료!");

          axiosInstance({
            headers: {
              Authorization: `Bearer ${token}`,
            },
            method: "GET",
            url: `/board`,
          })
            .then((res) => {
              console.log(res.data);
              setPresetCard(res.data.BoardListInfo);
              setSearchCard(res.data.BoardListInfo);
            })
            .catch((err) => {
              console.log(err);
            });
        })
        .catch((err) => {
          console.log(err);
        });
    }
  }

  const handleImageClick = (name) => {
    const isSelected = selectedImageId.includes(name);

    if (isSelected) {
      setSelectedImageId(selectedImageId.filter((imageId) => imageId !== name));
    } else {
      setSelectedImageId([name]);
    }
    console.log(selectedImageId);
  };

  const PresetImages = presetList.map((el) => {
    const presetUrl = `https://dm51j1y1p1ekp.cloudfront.net/preset/${el.presetImgSearchName}`;
    const isSelected = selectedImageId.includes(el.id);
    return (
      <div key={el.id} className="board_modal_temp" onClick={() => handleImageClick(el.id)}>
        <div
          className={`${
            isSelected ? "board_modal_image_selected" : ""
          } board_modal_image card`}
          style={{ marginBottom: "30px" }}
        >
          <header className="card-header" style={{ width: "100%" }}>
            <p
              className="card-header-title"
              style={{ fontSize: "22px", fontWeight: "bold" }}
            >
              {el.presetName}
            </p>
          </header>
          <div className={`is-16by9`}>
            <img src={presetUrl} alt="아오" />
          </div>
        </div>
        {isSelected && (
          <i
            className="fa-solid fa-check fa-bounce fa-5x board_check_icon"
            style={{ color: "#0aeb24" , cursor : "pointer"}}
          ></i>
        )}
      </div>
    );
  });

  const PresetCardImages = searchCard.map((el) => {
    // console.log(el)
    const presetUrl = `https://dm51j1y1p1ekp.cloudfront.net/preset/${el.presetImgSearchName}`;
    const check = el.userDto.profileImgSearchName;
    const profileImageUrl2 = `https://dm51j1y1p1ekp.cloudfront.net/profile/${el.userDto.profileImgSearchName}`;
    return (
      <div
        className="card board_card"
        key={el.boardSeq}
        onClick={() => navigate(`/board/${el.boardSeq}`)}
      >
        <div className="card-image">
          <figure className="image is-4by3">
            <img src={presetUrl ? presetUrl : "/withView2.png"} alt="없음" />
          </figure>
        </div>

        <div className="card-content" style={{ marginBottom: "5px" }}>
          <div className="media">
            <div className="media-left">
              <figure className="image is-48x48">
                <img
                  src={check ? profileImageUrl2 : "/withView2.png"}
                  alt="없음"
                />
              </figure>
            </div>
            <div className="media-content board_content">
              <p className="title is-6">{el.title}</p>
              <p className="subtitle is-6">{el.nickname}</p>
            </div>
          </div>
        </div>
      </div>
    );
  });

  function createContentStart() {
    setIsModalActive(true);
  }
  return (
    <div className="innermain">
      <ServerOptions
        profileImage={profileImage}
        profileNickname={profileNickname}
      />

      <hr className="serverOptionsLine_profile" />

      <div className={`modal ${isModalActive ? "is-active" : ""}`} id="myModal">
        <div
          className={`modal-background ${isModalActive ? "is-active" : ""}`}
          onClick={() => {
            setContent("");
            setTitle("");
            setSelectedImageId([]);
            setIsModalActive(false);
          }} // 모달 배경 클릭 시 모달 닫기
        ></div>
        <div className="modal-card board_modal_card">
          <header
            className="modal-card-head"
            style={{ backgroundColor: "white" }}
          >
            <p className="modal-card-title board_modal_card_title">
              창작마당 글쓰기
            </p>
            <button
              className="delete"
              aria-label="close"
              onClick={() => {
                setContent("");
                setTitle("");
                setSelectedImageId([]);
                setIsModalActive(false);
              }} // 모달 닫기 버튼 클릭 시 setIsModalActive(false) 호출
            ></button>
          </header>

          <section className="modal-card-body board_modal_cardbody">
            <p className="board_modal_card_inputtitle">제목</p>
            <input
              type="text"
              className="input board_modal_card_input"
              value={title}
              onChange={(e) => {
                setTitle(e.target.value);
              }}
            />
            <hr />
            <p className="board_modal_card_inputtitle">프리셋을 선택하세요!</p>
  
            {PresetImages.length > 0 ? (
              PresetImages
            ) : (
              <img src="/nullPreset.png" alt="아오" />
            )}

            <p className="board_modal_card_inputtitle">내용</p>
            <textarea
              className="textarea board_modal_card_content"
              value={content}
              placeholder="내용"
              onChange={(e) => {
                setContent(e.target.value);
              }}
            ></textarea>
          </section>

          <footer className="modal-card-foot board_modal_card_writebtn">
            <button className="button board_modal_button" onClick={WritePost}>
              작성
            </button>
          </footer>
        </div>
      </div>
      <div className="board_input">
        <i
          className="fa-solid fa-file-circle-plus board_write"
          style={{ fontSize: "1.7em" }}
          onClick={createContentStart}
        ></i>

        <p className="control has-icons-left board_search">
          <input
            className="input board_search_input"
            type="text"
            placeholder="Search"
            onChange={(e) => {
              setSearchCard(
                presetCard.filter((el) => {
                  console.log(el.title);
                  return el.title.includes(e.target.value);
                })
              );

              console.log(e.target.value);
              console.log(searchCard);
            }}
          />
          <span className="icon is-left">
            <i className="fas fa-search" aria-hidden="true"></i>
          </span>
        </p>
      </div>
      <div className="board_notice">{PresetCardImages}</div>
    </div>
  );
}

export default Board;
