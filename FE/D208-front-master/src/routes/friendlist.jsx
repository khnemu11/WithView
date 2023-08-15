import React, { useState, useEffect, useRef } from "react";
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
  const [chatMessages, setChatMessages] = useState([]); // 각 채팅방별 메시지를 저장

  const [chatRooms, setChatRooms] = useState([]);

  const [chatData, setChatData] = useState(null);

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

  const handleChatSelection = (chatRoomSeq) => {
    setSelectedChat(chatRoomSeq);
  };

  const connectStomp = async () => {
    return new Promise((resolve, reject) => {
      try {
        if (!stomp) {
          reject(new Error("Stomp 객체가 초기화되지 않았습니다."));
          return;
        }

        stomp.connect(
          {
            userSeq: userSeq,
          },
          () => {
            console.log("STOMP 연결 성공");
            resolve(stomp);
          },
          (error) => {
            console.error("STOMP 연결 중 에러 발생:", error); // 연결 중 발생하는 에러 로깅
            reject(error);
          }
        );
      } catch (error) {
        console.error("connectStomp에서 예외 발생:", error); // 기타 예외 로깅
        reject(error);
      }
    });
  };

  //1대1 채팅 목록에 들어간다.
  useEffect(() => {
    if (!stomp) {
      console.log("STOMP 객체가 아직 초기화되지 않았습니다.");
      return;
    }
    let chatRoomInfoSubscription;

    const recvchatroom = (recieve) => {
      console.log("setChatRoom 작동 중 작동 중");
      setChatRooms(recieve); // 받아온 정보를 chatRooms 상태에 저장
    };

    const fetchChatRoomInfo = async () => {
      try {
        await connectStomp();

        chatRoomInfoSubscription = stomp.subscribe(
          `/api/sub/chat/friends/chatroominfo/${userSeq}`,
          (message) => {
            var recieve = JSON.parse(message.body);
            recvchatroom(recieve);
            console.log("채팅방 목록 구독 중");
          },
          (error) => {
            console.error("구독 중 에러 발생:", error);
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
      console.log("채팅방 목록 구독 취소");
      chatRoomInfoSubscription && chatRoomInfoSubscription.unsubscribe(); // 컴포넌트가 언마운트될 때 구독 취소
    };
  }, [stomp, userSeq]);

  console.log("챗룸", chatRooms);

  console.log("챗룸 테스트", chatRooms.friendsChatRoomsUserInfoDtos);

  console.log("친구 테스트", friends);

  const filteredFriends = friends.filter((friend) =>
    friend.nickname.includes(searchTerm)
  );

  const filteredChatRooms = chatRooms.friendsChatRoomsUserInfoDtos
    ? chatRooms.friendsChatRoomsUserInfoDtos.filter((chat) =>
        chat.userDto.nickname.includes(searchTerm)
      )
    : [];

  console.log("filteredChatRooms", filteredChatRooms);

  const handleSearch = (event) => {
    if (event.key === "Enter") {
      setSearchTerm(event.target.value);
    }
  };

  const handleFriendBoxDoubleClick = async (friend) => {
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

      console.log("요청 성공:", response.data); // 서버 응답 출력
    } catch (error) {
      console.error("Error creating new chat:", error);
    }
  };

  const [currentSubscription, setCurrentSubscription] = useState(null);
  const [currentFriendsChatRoomSeq, setCurrentFriendsChatRoomSeq] =
    useState(null);

  function handleChatBoxDoubleClick(chat, page = 1) {
    setChatMessages([]);
    const friendsChatRoomSeq = chat.chatRoomSeq;

    setChatData(chat);
    console.log(
      "currentSubscriptioncurrentSubscriptioncurrentSubscription",
      currentSubscription
    );
    if (currentSubscription) {
      currentSubscription.unsubscribe();
      setInputMessage("");
      stomp.send(
        `/api/pub/chat/friends/${currentFriendsChatRoomSeq}/${userSeq}`,
        {},
        ""
      );
    }

    // 1. API를 사용하여 이전 채팅 기록 가져오기

    axiosInstance
      .get(`/chat/friends/${friendsChatRoomSeq}?page=${page}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((response) => {
        console.log("리스폰스데이터", response.data);
        const chatList = response.data.messages;
        if (Array.isArray(chatList) && chatList.length) {
          // chatList가 배열인지와 내용이 있는지 확인
          chatList.forEach((chat) => {
            recvMessage(chat);
          });
        }
      })
      .catch((error) => {
        console.error("Error fetching chat history:", error);
      });

    // 2. 웹소켓을 통한 실시간 채팅 데이터 수신
    const friendsChatSubscribe = stomp.subscribe(
      `/api/sub/chat/friends/${friendsChatRoomSeq}`,
      function (message) {
        const recieve = JSON.parse(message.body);
        recvMessage(recieve);
      }
    );
    stomp.send(`/api/pub/chat/friends/${friendsChatRoomSeq}`, {}, "");
    setCurrentSubscription(friendsChatSubscribe); // 상태 변수에 새 구독 저장
    setCurrentFriendsChatRoomSeq(friendsChatRoomSeq);

    handleChatSelection(friendsChatRoomSeq);

    // 해당 페이지나 대화방을 나갈 때 웹소켓 구독 해지
    window.addEventListener("beforeunload", function () {
      if (friendsChatSubscribe) {
        friendsChatSubscribe.unsubscribe();
        stomp.send(
          `/api/pub/chat/friends/${currentFriendsChatRoomSeq}/${userSeq}`,
          {},
          ""
        );
      }
    });
  }

  function recvMessage(chat) {
    // 메시지 상태 업데이트
    setChatMessages((prevMessages) => {
      return [...prevMessages, chat];
    });
  }

  useEffect(() => {
    console.log("최신 chatMessages 값:", chatMessages);
  }, [chatMessages]);

  const [inputMessage, setInputMessage] = useState("");

  const sendMessage = () => {
    console.log("이것은 챗데이터다", chatData);
    console.log("sendMessage 함수가 호출되었습니다.");

    const toUserSeq = chatData.userDto.seq;

    if (inputMessage.trim() !== "") {
      stomp.send(
        `/api/pub/chat/friends/message`,
        {},
        JSON.stringify({
          friendsChatRoomSeq: chatData.chatRoomSeq,
          message: inputMessage,
          fromUserSeq: userSeq,
          toUserSeq: toUserSeq,
        })
      );
      stomp.send(
        `/api/pub/chat/friends/chatroominfo`,
        {},
        JSON.stringify({ userSeq: toUserSeq })
      );
      stomp.send(
        `/api/pub/chat/friends/chatroominfo`,
        {},
        JSON.stringify({ userSeq: userSeq })
      );
      console.log("메시지 보내기 성공!");
      setInputMessage(""); // 메시지 전송 후 input 비우기
    }
  };

  const handleInputChange = (event) => {
    setInputMessage(event.target.value);
  };

  const handleKeyDown = (event) => {
    if (event.key === "Enter" && inputMessage.trim() !== "") {
      event.preventDefault(); // 기본 이벤트를 방지합니다.
      sendMessage(inputMessage.trim());
      setInputMessage(""); // 입력 필드를 초기화합니다.
    }
  };

  function ChatMessagesComponent({ chatMessages, userSeq }) {
    const chatContainerRef = useRef(null);
    let atBottomBeforeUpdate = true;

    useEffect(() => {
      if (chatContainerRef.current) {
        const element = chatContainerRef.current;
        const isAtBottom =
          element.scrollHeight - element.scrollTop === element.clientHeight;

        if (isAtBottom || atBottomBeforeUpdate) {
          element.scrollTop = element.scrollHeight;
        }

        atBottomBeforeUpdate = isAtBottom;
      }
    }, [chatMessages]);

    const formatDate = (dateTimeString) => {
      const date = new Date(dateTimeString);
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hours = String(date.getHours()).padStart(2, "0");
      const minutes = String(date.getMinutes()).padStart(2, "0");

      return { date: `${month}-${day}`, time: `${hours}:${minutes}` };
    };

    return (
      <div className="chat-messages" ref={chatContainerRef}>
        {chatMessages.map((chat, index) => {
          const isMe = chat.fromUserSeq === userSeq;
          const className = isMe ? "me-chat" : "other-chat";
          const className2 = isMe ? "me" : "";
          const { date, time } = formatDate(chat.sendTime);

          return (
            <div key={index} className={`messageBox ${className2}`}>
              {isMe && (
                <span className="send-time">
                  <span className="date">{date}</span>
                  <span className="time">{time}</span>
                </span>
              )}
              <div className={`message ${className}`}>
                <div className="message-content">{chat.message}</div>
              </div>
              {!isMe && (
                <span className="send-time">
                  <span className="date">{date}</span>
                  <span className="time">{time}</span>
                </span>
              )}
            </div>
          );
        })}
      </div>
    );
  }

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
                  key={chat.chatRoomSeq} // chatRoomSeq를 key로 사용
                  className="friend-box"
                  onDoubleClick={() => handleChatBoxDoubleClick(chat)}
                >
                  <img
                    src={
                      chat.userDto.profileImgSearchName
                        ? `https://dm51j1y1p1ekp.cloudfront.net/profile/${chat.userDto.profileImgSearchName}`
                        : "/withView2.png"
                    }
                    alt={chat.userDto.nickname}
                    className="friend-profile-image"
                  />
                  <span className="name">{chat.userDto.nickname}</span>
                  <span className="chat-last-message">
                    {chat.friendsChatMessageDto.message}
                  </span>
                </div>
              ))}
        </div>
        <div className="friendlist-right-pane">
          {selectedChat ? (
            <div className="chat-container">
              <div className="chat-header">
                {chatRooms &&
                  chatRooms.friendsChatRoomsUserInfoDtos &&
                  chatRooms.friendsChatRoomsUserInfoDtos
                    .filter((chat) => chat.chatRoomSeq === selectedChat)
                    .map((chatRoom, index) => (
                      <React.Fragment key={index}>
                        <img
                          src={
                            chatRoom.userDto.profileImgSearchName
                              ? `https://dm51j1y1p1ekp.cloudfront.net/profile/${chatRoom.userDto.profileImgSearchName}`
                              : "/withView2.png"
                          }
                          alt={chatRoom.userDto.nickname}
                          className="chat-profile-image"
                        />
                        <div className="chat-username">
                          {chatRoom.userDto.nickname}
                        </div>
                      </React.Fragment>
                    ))}
              </div>
              <ChatMessagesComponent
                chatMessages={chatMessages}
                userSeq={userSeq}
              />
              <div className="chat-input">
                <input
                  type="text"
                  placeholder="메시지 입력..."
                  value={inputMessage}
                  onChange={handleInputChange}
                  onKeyDown={handleKeyDown}
                />
                <img
                  src={paperPlaneIcon}
                  alt="Send"
                  className="send-icon"
                  onClick={sendMessage}
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
