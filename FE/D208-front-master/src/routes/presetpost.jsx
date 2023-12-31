import React from "react";
import ServerOptions from "./components/serveroptions";
import { useNavigate, useParams } from "react-router";
import { useSelector } from "react-redux";
import { useEffect, useState } from "react";
import "../css/presetpost.css";
import axiosInstance from "./axiosinstance";

function Presetpost() {
  const { seq } = useParams();
  const navigate = useNavigate();
  const profileNickname = useSelector((state) => state.user.nickname);
  const token = useSelector((state) => state.token);
  const userPk = useSelector((state) => state.user.seq);
  const [profileImage, setProfileImage] = useState(null);
  const profileImageURL = useSelector((state) => state.user.profile);
  const profileImageUrl = `https://dm51j1y1p1ekp.cloudfront.net/profile/${profileImageURL}`;
  const [postContent, setPostContent] = useState("");
  const presetPostImageUrl = `https://dm51j1y1p1ekp.cloudfront.net/preset/${postContent.presetImgSearchName}`;
  const profileImageUrl2 = `https://dm51j1y1p1ekp.cloudfront.net/profile/${postContent.profileImgSearchName}`;
  const [isModalActive, setIsModalActive] = useState(false);
  const [selectedImageId, setSelectedImageId] = useState([]);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [modifyButton, setModifyButton] = useState(false);
  const [presetList, setPresetList] = useState([]);
  const check = postContent.profileImgSearchName;
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
      url: `/preset/${userPk}/list`,
    })
      .then((res) => {
        setPresetList(res.data.PresetListInfo);
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);

  // 개별 게시물 get
  useEffect(() => {
    axiosInstance({
      headers: {
        Authorization: `Bearer ${token}`,
      },
      method: "GET",
      url: `/board/${seq}`,
    })
      .then((res) => {
        setPostContent(res.data.BoardInfo);
        if (res.data.BoardInfo.userSeq === userPk) {
          setModifyButton(true);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);

  function UpdatePost() {
    if (!selectedImageId[0]) {
      alert("프리셋을 선택해주세요!");
    } else if (title === "") {
      alert("제목을 입력해주세요!");
    } else {
      axiosInstance({
        headers: {
          Authorization: `Bearer ${token}`,
        },
        method: "PUT",
        url: `/board`,
        data: {
          boardSeq: seq,
          title: title,
          content: content,
          presetId: selectedImageId[0],
        },
      })
        .then(() => {
          
          alert("수정 완료!");
          setTitle("");
          setContent("");
          setSelectedImageId([]);
          setIsModalActive(false);

          axiosInstance({
            headers: {
              Authorization: `Bearer ${token}`,
            },
            method: "GET",
            url: `/board/${seq}`,
          })
            .then((res) => {
              
              setPostContent(res.data.BoardInfo);
              if (res.data.BoardInfo.userSeq === userPk) {
                setModifyButton(true);
              }
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
            style={{ color: "#0aeb24", cursor : "pointer" }}
          ></i>
        )}
      </div>
    );
  });

  const handleImageClick = (name) => {
    const isSelected = selectedImageId.includes(name);

    if (isSelected) {
      setSelectedImageId(selectedImageId.filter((imageId) => imageId !== name));
    } else {
      setSelectedImageId([name]);
    }
    
  };

  const UpdateContent = () => {
    setTitle(postContent.title);
    setContent(postContent.content);
    setSelectedImageId([postContent.presetId]);
    setIsModalActive(true);
  };

  const DeleteContent = () => {
    axiosInstance({
      headers: {
        Authorization: `Bearer ${token}`,
      },
      method: "DELETE",
      url: `/board/${seq}`,
    })
      .then(() => {
        
        alert("삭제완료!");
        navigate("/board");
      })
      .catch((err) => {
        console.log(err);
      });
  };

  async function imageUrlToBlob(url) {
    try {
      const response = await fetch(url);
      const blob = await response.blob();
      return blob;
    } catch (error) {
      console.error("이미지를 변환하는 동안 오류가 발생했습니다.", error);
      throw error; // 에러를 다시 던져서 상위 함수로 전달
    }
  }

  // 이미지 URL을 바로 파일로 만들어 axios 요청을 보내는 함수
  async function makeFileImg() {
    try {
      // 이미지 URL을 Blob으로 변환
      const blob = await imageUrlToBlob(presetPostImageUrl);

      // Blob을 File로 변환
      const file = new File([blob], "presetImage.png", {
        type: "image/png",
      });

      // axios 요청 보내기
      axiosInstance({
        headers: {
          "Content-Type": "multipart/form-data",
          Authorization: `Bearer ${token}`,
        },
        method: "POST",
        url: `/preset`,
        data: {
          userSeq: userPk,
          presetName: postContent.title,
          stage: postContent.stage,
          file: file,
        },
      })
        .then(() => {
         
          alert("저장완료 !!");
        })
        .catch((err) => {
          console.log(err);
        });
    } catch (error) {
      console.error("이미지를 변환하는 동안 오류가 발생했습니다.", error);
    }
  }

  return (
    <div className="innermain">
      <ServerOptions
        profileImage={profileImage}
        profileNickname={profileNickname}
      />

      <hr className="serverOptionsLine_profile" />
      <div className="card presetpost_card" style={{ marginTop: "30px" }}>
        <div className="card-image">
          <figure className="image is-4by3">
            <img src={presetPostImageUrl} />
          </figure>
        </div>
        <div className="card-content">
          <div className="media">
            <div className="media-left" style={{ marginRight: "25px" }}>
              <figure className="image is-96x96">
                <img src={check ? profileImageUrl2 : "/withView2.png"} />
              </figure>
            </div>

            <div className="media-content presetpost_media-content">
              <p className="title is-4">{postContent.title}</p>
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  height: "35%",
                }}
              >
                <p className="subtitle is-6">작성자 : {postContent.nickname}</p>
                <p className="subtitle is-6">
                  {postContent.registerTime
                    ? postContent.registerTime.substring(0, 10)
                    : ""}
                </p>
              </div>
            </div>
          </div>

          <div className="content">
            <blockquote style={{wordWrap:"break-word"}}>{postContent.content}</blockquote>
          </div>

          <div className="presetpost_buttons">
            <i
              className="fa-solid fa-chevron-left fa-3x presetpost_icon"
              onClick={() => navigate("/board")}
            ></i>

            <button
              className="button presetpost_icon"
              style={{
                height: "60px",
                fontSize: "20px",
                borderRadius: "10px",
                fontWeight: "bold",
                backgroundColor: "#769FCD",
                color: "white",
              }}
              onClick={makeFileImg}
            >
              프리셋 저장하기
              <i
                className="fa-solid fa-download fa-lg"
                style={{ marginLeft: "10px" }}
              ></i>
            </button>

            <div className={`${modifyButton ? "" : "is-hidden"}`}>
              <i
                className="fa-solid fa-pen-to-square fa-3x presetpost_icon"
                onClick={UpdateContent}
                style={{ marginRight: "20px" }}
              ></i>
              <i
                className="fa-solid fa-trash-can fa-3x presetpost_icon"
                onClick={DeleteContent}
                style={{ marginLeft: "20px" }}
              ></i>
            </div>
          </div>
        </div>
      </div>

      <div className={`modal ${isModalActive ? "is-active" : ""}`} id="myModal">
        <div
          className={`modal-background ${isModalActive ? "is-active" : ""}`}
          onClick={() => {
            setIsModalActive(false);
          }} // 모달 배경 클릭 시 모달 닫기
        ></div>
        <div className="modal-card board_modal_card">
          <header
            className="modal-card-head"
            style={{ backgroundColor: "white" }}
          >
            <p className="modal-card-title board_modal_card_title">
              창작마당 수정
            </p>
            <button
              className="delete"
              aria-label="close"
              onClick={() => {
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
            {PresetImages}

            <p className="board_modal_card_inputtitle">내용</p>
            <textarea
              className="textarea board_modal_card_content"
              placeholder="내용"
              value={content}
              onChange={(e) => {
                setContent(e.target.value);
              }}
            ></textarea>
          </section>

          <footer className="modal-card-foot board_modal_card_writebtn">
            <button className="button board_modal_button" onClick={UpdatePost}>
              작성
            </button>
          </footer>
        </div>
      </div>
    </div>
  );
}

export default Presetpost;
