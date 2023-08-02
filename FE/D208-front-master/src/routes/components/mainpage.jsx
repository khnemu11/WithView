import React, { useState, useEffect } from "react";
import Slider from "react-slick";
import Modal from "react-modal";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import "../../css/firstmain.css";
import "../../css/mainpage.css"; // CSS 파일 임포트
import ServerOptions from "./serveroptions";
import { useSelector } from "react-redux";
import axios from "axios";
import { Link } from "react-router-dom";

Modal.setAppElement("#root");

const Mainpage = () => {
  const [searchText, setSearchText] = useState("");
  const [profileImage, setProfileImage] = useState(null);
  const profileNickname = useSelector((state) => state.user.nickname);
  const profileImageURL = useSelector((state) => state.user.profile);
  const userSeq = useSelector((state) => state.user.seq);

  const [joinserverData, setJoinServerData] = useState([]);
  const [favoriteserverData, setFavoriteServerData] = useState([]);
  const [searchResults, setSearchResults] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    // 만약 redux에서 프로필 이미지가 null이면 기본 이미지로 설정
    if (profileImageURL === null) {
      setProfileImage("/withView2.png");
    } else {
      setProfileImage(profileImageURL);
    }
  }, [profileImageURL]);

  const handleSearchInputChange = (e) => {
    setSearchText(e.target.value);
  };

  const handleSearchIconClick = () => {
    // 검색 아이콘 클릭 시 검색 동작 추가 (원하는 로직으로 대체)
    // Filter the joinserverData based on the search text and set the search results.
    const results = joinserverData.filter((server) =>
      server.name.toLowerCase().includes(searchText.toLowerCase())
    );
    setSearchResults(results);

    // Open the modal.
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const handleSearchKeyPress = (e) => {
    if (e.key === "Enter") {
      handleSearchIconClick();
    }
  };

  useEffect(() => {
    // API를 통해 사용자가 참여한 서버 데이터를 가져오는 함수
    const fetchJoinedServers = async () => {
      try {
        const response = await axios.get(
          `https://i9d208.p.ssafy.io/api/servers/find-server-by-user?userSeq=${userSeq}`
        );
        const data = response.data;
        setJoinServerData(data.servers);
      } catch (error) {
        console.error("Error:", error);
      }
    };

    fetchJoinedServers();
  }, [userSeq]);

  useEffect(() => {
    // joinserverData에서 즐겨찾기된 서버를 필터링하여 설정
    const favorites = joinserverData.filter((server) => server.favorite);
    setFavoriteServerData(favorites);
  }, [joinserverData]);

  useEffect(() => {
    // joinserverData에서 검색 결과를 필터링하여 설정
    const results = joinserverData.filter((server) =>
      server.name.toLowerCase().includes(searchText.toLowerCase())
    );
    setSearchResults(results);
  }, [joinserverData, searchText]);

  const handleFavoriteToggle = async (serverSeq, favorite) => {
    try {
      const url = `https://i9d208.p.ssafy.io/api/users/1/favorites/`;

      const formData = new FormData();
      formData.append("userSeq", userSeq);
      formData.append("serverSeq", serverSeq);

      if (favorite) {
        // 이미 즐겨찾기에 추가된 서버이면 삭제 요청 (DELETE)
        await axios.delete(url, { data: formData });
      } else {
        // 즐겨찾기에 추가되지 않은 서버이면 추가 요청 (POST)
        await axios.post(url, formData);
      }

      // 서버의 즐겨찾기 상태가 토글되었으므로 joinserverData 상태를 업데이트합니다.
      setJoinServerData((prevData) =>
        prevData.map((server) =>
          server.seq === serverSeq ? { ...server, favorite: !favorite } : server
        )
      );
    } catch (error) {
      console.error("Error toggling favorite:", error);
    }
  };

  const CardItem = ({
    seq,
    name,
    limitChannel,
    hostSeq,
    backgroundImgSearchName,
    backgroundImgOriginalName,
    favorite,
  }) => {
    const handleCardClick = () => {
      // 서버 화면으로 이동하는 경로를 설정합니다.
      const serverLinkPath = `/server/${seq}`;
      // 클릭 시 서버로 이동합니다.
      window.location.href = serverLinkPath;
    };

    const handleFavoriteButtonClick = (e) => {
      e.stopPropagation();
      // 즐겨찾기 기능 작동
      handleFavoriteToggle(seq, favorite);
    };

    return (
      <div className="card custom-card-height" style={{ position: "relative" }}>
        <div
          className="card-image"
          onClick={handleCardClick}
          style={{ cursor: "pointer" }}
        >
          <figure className="image">
            <img
              src={`https://dm51j1y1p1ekp.cloudfront.net/server-background/${backgroundImgSearchName}`}
            />
          </figure>
        </div>
        <button className="star-button" onClick={handleFavoriteButtonClick}>
          <img src={favorite ? "/yellowstar.png" : "/whitestar.png"} />
        </button>
        <Link to={`/server/${seq}`} style={{ textDecoration: "none" }}>
          <header className="card-header">
            <p className="card-header-title">{name}</p>
          </header>
        </Link>
      </div>
    );
  };

  const sliderSettings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: favoriteserverData.length < 3 ? favoriteserverData.length : 3,
    slidesToScroll: 1,
  };

  return (
    <div className="mainbox">
      <div className="innermain">
        <ServerOptions
          profileImage={profileImage}
          profileNickname={profileNickname}
        />
        <hr className="serverOptionsLine_main" />

        {/* 검색 창과 아이콘 */}
        <div className="searchlayer">
          <div className="searchContainer">
            <input
              type="text"
              placeholder="서버 찾기"
              value={searchText}
              onChange={handleSearchInputChange}
              onKeyDown={handleSearchKeyPress} // 엔터키 입력 처리를 위한 이벤트 핸들러 추가
            />
            <img
              className="searchIcon"
              src="/searchicon.png"
              alt="Search"
              onClick={handleSearchIconClick}
            />
          </div>
        </div>
        {/* 모달 */}
        <Modal
          isOpen={isModalOpen}
          onRequestClose={closeModal}
          contentLabel="Search Results"
          className="serverSearchModal"
          overlayClassName="modal-overlay"
        >
          <h2 className="serverSearchText">서버 검색 결과</h2>
          <hr className="serverOptionsLine_main" />
          {searchResults.length > 0 ? (
            <div className="grid-container-modal">
              {searchResults.map((server) => (
                <div key={server.seq} className="grid-item">
                  <CardItem
                    seq={server.seq}
                    name={server.name}
                    limitChannel={server.limitChannel}
                    hostSeq={server.hostSeq}
                    backgroundImgSearchName={server.backgroundImgSearchName}
                    backgroundImgOriginalName={server.backgroundImgOriginalName}
                    favorite={server.favorite}
                  />
                </div>
              ))}
            </div>
          ) : (
            <p>검색 결과가 없습니다.</p>
          )}
        </Modal>
        {/* "즐겨찾기" 텍스트와 줄 */}
        <div className="lefttextlayer">
          <div className="favoriteText is-flex is-align-items-center">
            즐겨찾기
          </div>
        </div>
        <hr className="favoriteLine" />
        {/* 카드 슬라이더 */}
        <div className="sliderContainer">
          <Slider {...sliderSettings}>
            {favoriteserverData.map((server) => (
              <div key={server.seq} className="slide">
                <CardItem
                  seq={server.seq}
                  name={server.name}
                  limitChannel={server.limitChannel}
                  hostSeq={server.hostSeq}
                  backgroundImgSearchName={server.backgroundImgSearchName}
                  backgroundImgOriginalName={server.backgroundImgOriginalName}
                  favorite={server.favorite}
                />
              </div>
            ))}
          </Slider>
        </div>
        {/* "내가 참여한 서버" 텍스트와 줄 */}
        <div className="lefttextlayer">
          <div className="favoriteText is-flex is-align-items-center">
            내가 참여한 서버
          </div>
        </div>
        <hr className="favoriteLine" />
        {/* Scrollable Area */}
        <div className="scrollable-area">
          <div className="grid-container">
            {joinserverData.map((server) => (
              <div key={server.seq} className="grid-item">
                <CardItem
                  seq={server.seq}
                  name={server.name}
                  limitChannel={server.limitChannel}
                  hostSeq={server.hostSeq}
                  backgroundImgSearchName={server.backgroundImgSearchName}
                  backgroundImgOriginalName={server.backgroundImgOriginalName}
                  favorite={server.favorite}
                />
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Mainpage;
