import React, { useState, useEffect } from "react";
import "../css/friendlist.css";
import friendAddIcon from "/friend-add.png";
import searchIcon from "/searchicon.png";
import paperPlaneIcon from "/paper-plane.png";
import { useSelector } from "react-redux";
import axios from "axios";
import axiosInstance from "./axiosinstance";

const FriendList = () => {
  const [selectedTab, setSelectedTab] = useState("친구"); // 초기 상태를 '친구'로 설정
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedChat, setSelectedChat] = useState(null);
  const userSeq = useSelector((state) => state.user.seq);
  const token = useSelector((state) => state.token);

  const [friends, setFriends] = useState([]);

  useEffect(() => {
    document.body.style.backgroundImage = "none";

    return () => {
      document.body.style.backgroundImage = "url(/background.png)";
    };
  }, []);

  useEffect(() => {
    const fetchFriends = async () => {
      try {
        const response = await axiosInstance.get(
          `/friends?userSeq=${userSeq}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setFriends(response.data.userList);
        console.log("친구목록 불러와졌습니다.");
      } catch (error) {
        console.error("Error fetching friends:", error);
      }
    };

    fetchFriends();
  }, [userSeq, token]);

  const chatRooms = [
    { id: 1, name: "John Doe", profileImage: null, lastMessage: "안녕하세요!" },
  ];

  const chatMessages = {
    "John Doe": [
      { sender: "John Doe", message: "안녕하세요 ?" },
      { sender: "Me", message: "안녕하세요 !" },
      { sender: "Me", message: "이것은 예시랍니다 !" },
      { sender: "John Doe", message: "그렇군요 ?" },
    ],
    // ... 기타 채팅방 메시지
  };

  const [chats, setChats] = useState(chatRooms);

  const filteredFriends = friends.filter((friend) =>
    friend.nickname.includes(searchTerm)
  );

  const filteredChatRooms = chats.filter((chat) =>
    chat.name.includes(searchTerm)
  );

  const handleSearch = (event) => {
    if (event.key === "Enter") {
      setSearchTerm(event.target.value);
    }
  };

  const handleFriendBoxDoubleClick = (friend) => {
    if (!chats.find((chat) => chat.name === friend.name)) {
      const newChat = { ...friend, lastMessage: "" }; // 초기 메시지는 빈 문자열로 설정
      setChats((prevChats) => [...prevChats, newChat]);
    }
  };

  const handleChatBoxDoubleClick = (chat) => {
    setSelectedChat(chat.name); // 채팅방 선택 시 해당 채팅방 이름으로 상태 업데이트
  };

  return (
    <div className="friendlist-container">
      <div className="friendlist-header">
        <button
          className={`friendlist-btn ${selectedTab === "친구" ? "active" : ""}`}
          onClick={() => setSelectedTab("친구")}
        >
          친구
        </button>
        <div className="friendlist-divider"></div> {/* 세로 줄 요소 추가 */}
        <button
          className={`friendlist-btn ${selectedTab === "채팅" ? "active" : ""}`}
          onClick={() => setSelectedTab("채팅")}
        >
          채팅
        </button>
        <img
          src={friendAddIcon}
          alt="Add Friend"
          className="friendlist-add-icon"
        />
      </div>
      <div className="friendlist-content">
        <div className="friendlist-left-pane">
          <div className="search-container">
            <input
              type="text"
              placeholder="검색"
              onKeyUp={handleSearch}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
            <img
              src={searchIcon}
              alt="Search"
              className="search-icon"
              onClick={() =>
                setSearchTerm(document.querySelector(".search-input").value)
              }
            />
          </div>
          {selectedTab === "친구"
            ? filteredFriends.map((friend) => (
                <div
                  key={friend.seq}
                  className="friend-box"
                  onDoubleClick={() => handleFriendBoxDoubleClick(friend)}
                >
                  <img
                    src={`https://dm51j1y1p1ekp.cloudfront.net/profile/${friend.profileImgSearchName}`}
                    alt={friend.nickname}
                    className="friend-profile-image"
                    onError={(e) => {
                      if (e.target.src !== "/withView2.png") {
                        e.target.src = "/withView2.png";
                      }
                    }}
                  />
                  <div className="friend-name">{friend.nickname}</div>
                  <div className="friend-status">{friend.profileMsg}</div>
                </div>
              ))
            : filteredChatRooms.map((chat) => (
                <div
                  key={chat.id}
                  className="friend-box"
                  onDoubleClick={() => handleChatBoxDoubleClick(chat)}
                >
                  <img
                    src={chat.profileImage || "/withView2.png"}
                    alt={chat.name}
                    className="friend-profile-image"
                  />
                  <span className="name">{chat.name}</span>
                  <span className="chat-last-message">{chat.lastMessage}</span>
                </div>
              ))}
        </div>
        <div className="friendlist-right-pane">
          {selectedChat ? (
            <div className="chat-container">
              <div className="chat-header">
                <img
                  src={
                    chatMessages[selectedChat][0].sender.profileImage ||
                    "/withView2.png"
                  } // 해당 친구의 프로필 이미지를 가져옵니다. 기본 이미지로 설정된 경우 기본 이미지가 나올 것입니다.
                  alt={selectedChat}
                  className="chat-profile-image"
                />
                <div className="chat-username">{selectedChat}</div>
              </div>
              <div className="chat-messages">
                {chatMessages[selectedChat].map((msg, index) => (
                  <div
                    key={index}
                    className={`message ${
                      msg.sender === "Me" ? "me-chat" : "other-chat"
                    }`}
                  >
                    <span>{msg.message}</span>
                  </div>
                ))}
              </div>
              <div className="chat-input">
                <input type="text" placeholder="메시지 입력..." />
                <img
                  src={paperPlaneIcon}
                  alt="Send"
                  className="send-icon"
                  // onClick={/* 메시지 전송 함수 */}
                />
              </div>
            </div>
          ) : (
            <img
              src="/withView.png"
              alt="No chat selected"
              className="NoChatImg"
            />
          )}
        </div>
      </div>
    </div>
  );
};

export default FriendList;
