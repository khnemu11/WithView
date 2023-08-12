import React, { useState, useEffect } from "react";
import "../css/friendlist.css";
import friendAddIcon from "/friend-add.png";
import searchIcon from "/searchicon.png";
import paperPlaneIcon from "/paper-plane.png";
import { useSelector } from "react-redux";
import axios from "axios";
import axiosInstance from "./axiosinstance";
import Checkwebsocket from "./components/checkwebsocket";

const FriendList = () => {
  const [selectedTab, setSelectedTab] = useState("친구"); // 초기 상태를 '친구'로 설정
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedChat, setSelectedChat] = useState(null);
  const userSeq = useSelector((state) => state.user.seq);
  const token = useSelector((state) => state.token);

  const [friends, setFriends] = useState([]);

  Checkwebsocket();

  const stomp = useSelector((state) => state.stomp);
  console.log("ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ", stomp);
  console.log("유저세크", userSeq);

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

  const [chatRooms, setChatRooms] = useState([]);
  const connectStomp = async () => {
    return new Promise((resolve, reject) => {
      try {
        if (!stomp) {
          reject(new Error("Stomp 객체가 초기화되지 않았습니다."));
          return;
        }
  
        stomp.connect(
          {
            "userSeq" : userSeq},
          () => {
            resolve(stomp);
          },
          (error) => {
            console.error('STOMP 연결 중 에러 발생:', error); // 연결 중 발생하는 에러 로깅
            reject(error);
          }
        );
      } catch (error) {
        console.error('connectStomp에서 예외 발생:', error); // 기타 예외 로깅
        reject(error);
      }
    });
  };

  useEffect(() => {
    if (!stomp) {
      console.log("STOMP 객체가 아직 초기화되지 않았습니다.");
      return;
    }
    let chatRoomInfoSubscription;

    const recvMessage = (recieve) => {
      setChatRooms(recieve); // 받아온 정보를 chatRooms 상태에 저장
    };

    const fetchChatRoomInfo = async () => {
      try {
        await connectStomp();

        chatRoomInfoSubscription = stomp.subscribe(
          `/api/sub/chat/friends/chatroominfo/${userSeq}`,
          (message) => {
            var recieve = JSON.parse(message.body);
            recvMessage(recieve);
          }
        );

        console.log("userSeq", userSeq);

        // 최초 메시지 요청
        stomp.send(
          `/api/pub/chat/friends/chatroominfo`,
          {},
          JSON.stringify({ userSeq: userSeq })
        );
      } catch (error) {
        console.error("STOMP 연결에 실패했습니다:", error);
      }
    };

    fetchChatRoomInfo();

    return () => {
      chatRoomInfoSubscription && chatRoomInfoSubscription.unsubscribe(); // 컴포넌트가 언마운트될 때 구독 취소
    };
  }, [stomp, userSeq]);

  const chatMessages = {
    "John Doe": [
      { sender: "John Doe", message: "안녕하세요 ?" },
      { sender: "Me", message: "안녕하세요 !" },
      { sender: "Me", message: "이것은 예시랍니다 !" },
      { sender: "John Doe", message: "그렇군요 ?" },
    ],
    // ... 기타 채팅방 메시지
  };

  console.log('챗룸', chatRooms)

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

  const handleFriendBoxDoubleClick = async (friend) => {
    if (!chats.find((chat) => chat.nickname === friend.nickname)) {
      try {
        const response = await axiosInstance.post(
          "/chat/friends",
          {
            mySeq: userSeq, // 현재 사용자의 seq
            yourSeq: friend.seq, // 클릭한 친구의 seq
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        // 응답에 따라서 추가 작업을 수행할 수 있습니다.
        // 예를 들면, 서버에서 새로운 채팅방 정보를 반환할 경우
        // 해당 정보를 chats에 추가할 수 있습니다.
        if (response.data && response.data.newChat) {
          setChats((prevChats) => [...prevChats, response.data.newChat]);
        }
        console.log("요청 성공");
      } catch (error) {
        console.error("Error creating new chat:", error);
      }
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
