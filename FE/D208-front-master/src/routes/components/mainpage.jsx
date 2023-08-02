import React, { useState, useEffect } from "react";
import Slider from "react-slick";
import Modal from "react-modal";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import "../../css/firstmain.css";
import "../../css/mainpage.css"; // CSS 파일 임포트
import ServerOptions from "./serveroptions";
import { useSelector } from "react-redux";

Modal.setAppElement("#root");

const Mainpage = () => {
  const [searchText, setSearchText] = useState("");
  const [profileImage, setProfileImage] = useState(null);
  const profileNickname = useSelector((state) => state.user.nickname)
  const profileImageURL = useSelector((state) => state.user.profile);

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

  const [searchResults, setSearchResults] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleSearchIconClick = () => {
    // 검색 아이콘 클릭 시 검색 동작 추가 (원하는 로직으로 대체)
    // Filter the joinserverData based on the search text and set the search results.
    const results = joinserverData.filter((server) =>
      server.title.toLowerCase().includes(searchText.toLowerCase())
    );
    setSearchResults(results);

    // Open the modal.
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const [joinserverData, setJoinServerData] = useState([
    // 기존 joinserverData의 내용
    {
      id: 1,
      title: "Card 1",
      content: "This is card 1 content.",
      imageSrc: "/놀이터.png", // 카드 1 이미지 파일 경로
      isFavorite: true,
    },
    {
      id: 2,
      title: "Card 2",
      content: "This is card 2 content.",
      imageSrc: "/롤.jpg", // 카드 2 이미지 파일 경로
      isFavorite: true,
    },
    // Add more card objects as needed
    {
      id: 3,
      title: "Card 3",
      content: "This is card 3 content.",
      imageSrc: "/고양이.jpg", // 카드 3 이미지 파일 경로
      isFavorite: true,
    },
    {
      id: 4,
      title: "Card 4",
      content: "This is card 4 content.",
      imageSrc: "/에이펙스.jpg", // 카드 4 이미지 파일 경로
      isFavorite: true,
    },
    {
      id: 5,
      title: "Card 5",
      content: "This is card 5 content.",
      imageSrc: "/개방.png", // 카드 4 이미지 파일 경로
      isFavorite: false,
    },
    {
      id: 6,
      title: "Card 6",
      content: "This is card 6 content.",
      imageSrc: "/풀방.png", // 카드 4 이미지 파일 경로
      isFavorite: false,
    },
  ]);

  const [favoriteserverData, setFavoriteServerData] = useState([]);

  useEffect(() => {
    const favorites = joinserverData.filter((server) => server.isFavorite);
    setFavoriteServerData(favorites);
  }, [joinserverData]);

  useEffect(() => {
    const results = joinserverData.filter((server) =>
      server.title.toLowerCase().includes(searchText.toLowerCase())
    );
    setSearchResults(results);
  }, [joinserverData, searchText]);

  const handleFavoriteToggle = (id) => {
    // joinserverData 상태를 업데이트하여 isFavorite 값을 토글합니다.
    setJoinServerData((prevData) =>
      prevData.map((server) =>
        server.id === id
          ? { ...server, isFavorite: !server.isFavorite }
          : server
      )
    );
  };

  const CardItem = ({
    id,
    title,
    content,
    imageSrc,
    isFavorite,
    isJoinServerData,
  }) => {
    console.log(imageSrc);
    return (
      <div className="card custom-card-height" style={{ position: "relative" }}>
        <div className="card-image">
          <figure className="image">
            <img src={imageSrc} />
          </figure>
        </div>
        <button
          className="star-button"
          onClick={() => handleFavoriteToggle(id)}
        >
          <img src={isFavorite ? "/yellowstar.png" : "/whitestar.png"} />
        </button>
        <header className="card-header">
          <p className="card-header-title">{title}</p>
        </header>
        {/* <div className="card-content">
          <div className="content">{content}</div>
        </div> */}
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
          <h2>서버 검색 결과</h2>
          {searchResults.length > 0 ? (
            <div className="grid-container-modal">
              {searchResults.map((card) => (
                <div key={card.id} className="grid-item">
                  <CardItem
                    id={card.id}
                    title={card.title}
                    content={card.content}
                    imageSrc={card.imageSrc}
                    isFavorite={card.isFavorite}
                  />
                </div>
              ))}
            </div>
          ) : (
            <p>검색 결과가 없습니다.</p>
          )}
          <button onClick={closeModal}>Close</button>
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
            {favoriteserverData.map((card) => (
              <div key={card.id} className="slide">
                <CardItem
                  id={card.id}
                  title={card.title}
                  content={card.content}
                  imageSrc={card.imageSrc}
                  isFavorite={card.isFavorite}
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
            {joinserverData.map((card) => (
              <div key={card.id} className="grid-item">
                <CardItem
                  id={card.id}
                  title={card.title}
                  content={card.content}
                  imageSrc={card.imageSrc}
                  isFavorite={card.isFavorite}
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
