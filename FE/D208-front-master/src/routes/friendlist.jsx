import React, { useState } from "react";
import "../css/friendlist.css";
import friendAddIcon from "/friend-add.png";
import searchIcon from "/searchicon.png";
import { useEffect } from "react";

const FriendList = () => {
  const [selectedTab, setSelectedTab] = useState("친구"); // 초기 상태를 '친구'로 설정
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedChat, setSelectedChat] = useState(null);

  useEffect(() => {
    document.body.style.backgroundImage = "none";

    return () => {
      document.body.style.backgroundImage = "url(/background.png)";
    };
  }, []);

  const friends = [
    { id: 1, name: "John Doe", profileImage: null, status: "Happy coding!" },
    {
      id: 2,
      name: "Jane Smith",
      profileImage: null,
      status: "Working on a new project!",
    },
    { id: 3, name: "Chris Evans", profileImage: null, status: "At the gym" },
    {
      id: 4,
      name: "Emily Clark",
      profileImage: null,
      status: "Reading a good book",
    },
    {
      id: 5,
      name: "Robert Downey",
      profileImage: null,
      status: "Just chilling",
    },
    {
      id: 6,
      name: "Emma Stone",
      profileImage: null,
      status: "Looking for a good movie to watch",
    },
    { id: 7, name: "Tom Holland", profileImage: null, status: "Busy shooting" },
    {
      id: 8,
      name: "Scarlett Johansson",
      profileImage: null,
      status: "On a vacation",
    },
    {
      id: 9,
      name: "Ryan Reynolds",
      profileImage: null,
      status: "Having fun with kids",
    },
    {
      id: 10,
      name: "Jennifer Lawrence",
      profileImage: null,
      status: "Cooking some delicious food",
    },
    {
      id: 11,
      name: "Bradley Cooper",
      profileImage: null,
      status: "Recording a new song",
    },
    {
      id: 12,
      name: "Angelina Jolie",
      profileImage: null,
      status: "Traveling the world",
    },
    {
      id: 13,
      name: "Hugh Jackman",
      profileImage: null,
      status: "Doing what I love",
    },
    {
      id: 14,
      name: "Natalie Portman",
      profileImage: null,
      status: "Thinking of a new role",
    },
    {
      id: 15,
      name: "Leonardo DiCaprio",
      profileImage: null,
      status: "In the wild",
    },
    {
      id: 16,
      name: "Meryl Streep",
      profileImage: null,
      status: "Looking for script",
    },
    {
      id: 17,
      name: "Tom Hanks",
      profileImage: null,
      status: "Playing with my dog",
    },
    {
      id: 18,
      name: "Charlize Theron",
      profileImage: null,
      status: "Yoga time",
    },
    { id: 19, name: "Matt Damon", profileImage: null, status: "Writing" },
    {
      id: 20,
      name: "Julia Roberts",
      profileImage: null,
      status: "Enjoying family time",
    },
    // ... 기타 친구들
  ];

  const chatRooms = [
    { id: 1, name: "John Doe", profileImage: null, lastMessage: "안녕하세요!" },
  ];

  const chatMessages = {
    "John Doe": [
      { sender: "John Doe", message: "안녕하세요?" },
      { sender: "Me", message: "안녕하세요!" },
      // ... 기타 메시지
    ],
    // ... 기타 채팅방 메시지
  };

  const [chats, setChats] = useState(chatRooms);

  const filteredFriends = friends.filter((friend) =>
    friend.name.includes(searchTerm)
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
                  key={friend.id}
                  className="friend-box"
                  onDoubleClick={() => handleFriendBoxDoubleClick(friend)}
                >
                  <img
                    src={friend.profileImage || "/withView2.png"}
                    alt={friend.name}
                    className="friend-profile-image"
                  />
                  <div className="friend-name">{friend.name}</div>
                  <div className="friend-status">{friend.status}</div>
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
                      msg.sender === "Me" ? "me" : "other"
                    }`}
                  >
                    <span>{msg.sender}: </span>
                    <span>{msg.message}</span>
                  </div>
                ))}
              </div>
              <div className="chat-input">
                <input type="text" placeholder="메시지 입력..." />
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
