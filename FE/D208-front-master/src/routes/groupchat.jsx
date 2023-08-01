import { useState, useEffect, useRef } from "react";
import { Link } from "react-router-dom";
import Konva from "konva";
import { OpenVidu } from "openvidu-browser";
import SockJS from "sockjs-client";
import man from "../assets/man.jpg";
import micon from "../assets/micon.png";
// import micoff from "../assets/micoff.png";
import volon from "../assets/volon.png";
// import voloff from "../assets/voloff.png";
import camon from "../assets/camon.png";
import camoff from "../assets/camoff.png";
import settings from "../assets/gear.png";
import sticker from "../assets/sticker.png";
import chat from "../assets/chat.png";
import back from "../assets/back.png";
import exit from "../assets/exit.png";
// import room from "../assets/room.png";
import room2 from "../assets/room2.jpg";
import pool from "../assets/pool.png";
import "../css/groupchat.css";
import axios from "axios";

export default function GroupChat() {
  const [backClicked, setbackClicked] = useState(false);
  const [profileClicked, setprofileClicked] = useState(false);
  const [micClicked, setmicClicked] = useState(false);
  const [volClicked, setvolClicked] = useState(false);
  const [camClicked, setcamClicked] = useState(false);
  const [settingsClicked, setsettingsClicked] = useState(false);
  const [stickerClicked, setstickerClicked] = useState(false);
  const [stickermenuClicked, setstickermenuClicked] = useState(false);
  const [chatClicked, setchatClicked] = useState(false);
  const [msgClicked, setmsgClicked] = useState(false);
  const [acc_volClicked, setacc_volClicked] = useState(false);
  const [acc_chClicked, setacc_chClicked] = useState(false);
  const [acc_ch_name, setacc_ch_name] = useState();
  const [isCameraOn, setIsCameraOn] = useState(true);
  function backSettings() {
    setbackClicked((prevbackClicked) => !prevbackClicked);
    setprofileClicked(false);
    setmicClicked(false);
    setvolClicked(false);
  }

  function profileSettings() {
    setprofileClicked((prevprofileClicked) => !prevprofileClicked);
    setmicClicked(false);
    setvolClicked(false);
  }

  function micSettings() {
    setmicClicked((prevmicClicked) => !prevmicClicked);
    setprofileClicked(false);
    setvolClicked(false);
  }

  function volSettings() {
    setvolClicked((prevvolClicked) => !prevvolClicked);
    setmicClicked(false);
    setprofileClicked(false);
  }

  function camSettings() {
    setcamClicked((prevcamClicked) => !prevcamClicked);
    if (publisherRef.current) {
      console.log(publisherRef.current);
      console.log(publisherRef.current.stream);
      const isCameraOn = !publisherRef.current.stream.videoActive;
      publisherRef.current.publishVideo(isCameraOn);
      setIsCameraOn(isCameraOn);
    }
    setmicClicked(false);
    setvolClicked(false);
    setprofileClicked(false);
  }

  function settingsSettings() {
    setsettingsClicked((prevsettingsClicked) => !prevsettingsClicked);
    setstickerClicked(false);
    setchatClicked(false);
  }

  function stickerSettings() {
    setstickerClicked((prevstickerClicked) => !prevstickerClicked);
    setsettingsClicked(false);
    setchatClicked(false);
  }

  function stickermenuSettings() {
    setstickermenuClicked((prevstickermenuClicked) => !prevstickermenuClicked);
  }

  function chatSettings() {
    setchatClicked((prevchatClicked) => !prevchatClicked);
    setstickerClicked(false);
    setsettingsClicked(false);
  }

  function msgSettings() {
    setmsgClicked((prevmsgClicked) => !prevmsgClicked);
  }

  function acc_volSettings() {
    setacc_volClicked((prevacc_volClicked) => !prevacc_volClicked);
  }

  function acc_chSettings() {
    setacc_chClicked((prevacc_chClicked) => !prevacc_chClicked);
  }

  function setOnline() {
    localStorage.setItem("state", "온라인");
  }

  function setOffline() {
    localStorage.setItem("state", "오프라인");
  }

  const handleInputChange = (event) => {
    setacc_ch_name(event.target.value);
  };

  const chnameChange = () => {
    axios
      .put("https://api.example.com/channels/123", { name: acc_ch_name })
      .then((response) => {
        console.log(response);
      })
      .catch((error) => {
        console.log("PUT 요청 에러:", error);
      });
  };

  //konva 오브젝트 아이디 룰
  //이미지 : img-로 시작
  //영상 : 이용자의 아이디

  let OV; //오픈비두 변수
  // let session; //현재 채널 이름(오픈비두에선 채팅방 단위를 'session'이라고 부름)
  let videoContainer = document.querySelector("#video-container"); //오픈비두로 받은 영상을 담은 컨테이너
  let port = 9091; //백엔드 포트 번호
  let domain = "localhost"; //도메인 주소
  let APPLICATION_SERVER_URL = `http://${domain}:${port}/`;
  let userId; //유저 아이디
  let remoteBGLayer = new Konva.Layer(); //소켓에 저장된 비디오 레이어(최초 접속시 한번 사용)
  let remoteVideoLayer = new Konva.Layer(); //소켓에 저장된 비디오 레이어(최초 접속시 한번 사용)
  let remoteImageLayer = new Konva.Layer(); //소켓에 저장된 이미지 레이어(최초 접속시 한번 사용)
  let stage = useRef(null);
  let session = useRef(null);
  let socket = useRef(null);
  let publisherRef = useRef(null);

  useEffect(() => {
    const container = document.getElementById("channel-screen");
    if (container) {
      stage.current = new Konva.Stage({
        container: "channel-screen",
        width: document.body.offsetWidth,
        height: document.body.offsetHeight,
      });
    }

    // Stage에 대한 추가 작업 수행
    // 배경 레이어 추가
    const backLayer = new Konva.Layer();
    stage.current.add(backLayer);

    // 비디오 레이어 추가
    const videoLayer = new Konva.Layer();
    stage.current.add(videoLayer);

    // 이미지 레이어 추가
    const imageLayer = new Konva.Layer();
    stage.current.add(imageLayer);

    // 트랜스포머를 그냥 선언
    let tr = new Konva.Transformer();
    videoLayer.add(tr);
    imageLayer.add(tr);

    videoLayer.on("click", function (e) {
      // 클릭한 요소를 가져오고 해당 요소를 Transformer에 설정
      let clickedShape = e.target;
      tr.nodes([clickedShape]);

      // 레이어 다시 그리기
      videoLayer.batchDraw();
    });

    imageLayer.on("click", function (e) {
      // 클릭한 요소를 가져오고 해당 요소를 Transformer에 설정
      let clickedShape = e.target;
      tr.nodes([clickedShape]);

      // 레이어 다시 그리기
      imageLayer.batchDraw();
    });

    backLayer.on("click", function () {
      tr.nodes([]);

      // 레이어 다시 그리기
      backLayer.batchDraw();
    });

    //요청 들어온 캔버스(보여주지 않으므로 크기를 0으로)
    let remoteStage = new Konva.Stage({
      container: "shared-screen",
      width: 0,
      height: 0,
    });
    remoteStage.hide();
  }, []);

  // let socket; //소켓 통신 변수
  //소켓 메시지가 오면 반응하는 메소드
  //소켓을 통신하는 것들 (채팅, 캔버스 json파일)
  async function onMessage(message) {
    console.log("message from socket!");
    //받은 소켓 메시지를 JSON파일로 파싱
    let data = JSON.parse(message.data);
    console.log(data);

    //캔버스 내용이 변경되면 실행
    if (data.type == "change") {
      console.log("캔버스 변화");
      console.log(data);
      loadCanvasChange(data.object);
    }
    //처음 채널에 참가하면 실행
    else if (data.type == "join") {
      //배경 레이어 로드
      if (data.BG != null) {
        console.log("서버에 있는 배경 레이어");
        remoteBGLayer = Konva.Node.create(data.BG);
        console.log(remoteBGLayer);
      }
      //비디오 레이어 로드
      if (data.video != null) {
        console.log("서버에 있는 비디오 레이어");
        remoteVideoLayer = Konva.Node.create(data.video);
        console.log(remoteVideoLayer);
      }
      //이미지 레이어 로드
      if (data.image != null) {
        console.log("서버에 있는 이미지 레이어");
        remoteImageLayer = Konva.Node.create(data.image);
        console.log(remoteImageLayer);
        let images = remoteImageLayer.find("Image");

        for (let i = 0; i < images.length; i++) {
          let tr = new Konva.Transformer();
          // tr.nodes([images[i]]);

          //이미지 변수 생성
          let imageObj = new Image();
          let imageName = images[i].getAttr("id").substring(4); //img- 제거한 나머지를 이름으로 설정
          console.log(imageName);
          imageObj.src = "https://dm51j1y1p1ekp.cloudfront.net/" + imageName;

          //로딩돠면
          imageObj.onload = function () {
            images[i].setAttr("image", imageObj);
          };

          //드래그 반응이 끝나면 캔버스 넘기기
          images[i].on("dragend", function () {
            changeCanvas(images[i]);
          });

          //비디오의 모양을 변경하면 캔버스 변경사항을 다른사람에게 전송
          images[i].on("transformend", function () {
            changeCanvas(images[i]);
          });
          console.log(stage.current);

          stage.current.children[2].add(tr);
          stage.current.children[2].add(images[i]);
        }
      }
    }
  }

  // 배경에 이미지 추가
  function addBackImage(imageUrl) {
    const backObj = new Image();
    backObj.onload = function () {
      const backgroundImage = new Konva.Image({
        id: imageUrl.substring(12),
        image: backObj,
        width: document.body.offsetWidth,
        height: document.body.offsetHeight,
      });

      stage.current.children[0].removeChildren(); // 모든 자식 제거
      stage.current.children[0].add(backgroundImage);
      stage.current.children[0].draw(); // 배경 레이어 다시 그리기
      console.log("이미지 로드!");

      console.log(session.current);
      changeCanvas(backgroundImage);
    };
    backObj.src = imageUrl;
  }

  const addBackImageClick = (imageUrl) => {
    console.log("배경 이미지 변환");
    console.log(session.current);

    addBackImage(imageUrl);
  };

  //켄버스에 변경 내용을 저장하는 함수
  function loadCanvasChange(data) {
    let object = Konva.Node.create(data); //변경된 오브젝트
    let objectId = object.getAttr("id");
    console.log("소캣에서 넘어온 객체 아이디");
    console.log(objectId);
    console.log(stage.current);

    let target = stage.current.find("#" + objectId); //객체 탐색

    console.log("현재 대상 객체");
    console.log(target);

    //기존 스테이지에 없는경우(이미지가 추가된 경우)
    if (target.length == 0) {
      //이미지 삽입
      if (objectId.indexOf("img-") != -1) {
        let tr = new Konva.Transformer();
        // tr.nodes([object]);

        //이미지 변수 생성
        let imageObj = new Image();
        let imageName = objectId.substring(4); //img- 제거한 나머지를 이름으로 설정
        imageObj.src = "https://dm51j1y1p1ekp.cloudfront.net/" + imageName;

        //로딩돠면
        imageObj.onload = function () {
          object.setAttr("image", imageObj);
        };

        //드래그 반응이 끝나면 캔버스 넘기기
        object.on("dragend", function () {
          changeCanvas(object);
        });

        //비디오의 모양을 변경하면 캔버스 변경사항을 다른사람에게 전송
        object.on("transformend", function () {
          changeCanvas(object);
        });

        stage.current.children[2].add(tr);
        stage.current.children[2].add(object);
      }

      // 배경 삽입
      else {
        //배경 변수 생성
        let backObj = new Image();
        let imageName = objectId; // 임시 어차피 바꿔야함
        backObj.src =
          "https://dm51j1y1p1ekp.cloudfront.net/channel-background/" +
          imageName;

        //로딩돠면
        backObj.onload = function () {
          object.setAttr("image", backObj);
        };

        stage.current.children[0].add(object);
      }
    } else {
      //이미지 위치 변경
      if (objectId.indexOf("img-") != -1) {
        target[0].setAttrs(object.getAttrs());
      }
      //영상 위치 변경
      else {
        target[0].setAttrs(object.getAttrs());
      }
    }
  }

  // 소켓에서 나갔을 때
  function onClose() {
    console.log("소켓 종료");
  }
  // 소켓으로 들어왔을 때
  function onOpen() {
    console.log("소켓 접속");
  }

  //오픈비두 기본함수
  //채널에 들어오면 sessionId와 userName(유저 아이디)를 통해 채널에 들어가는 로직이 들어가 있는 함수
  //제일 중요함
  /* OPENVIDU METHODS */
  function joinSession(event) {
    event.preventDefault();
    let mySessionId = document.getElementById("sessionId").value;
    let myUserName = document.getElementById("userName").value;

    //참가한 채널 명을 url로 구분하도록 커스터마이징함
    socket.current = new SockJS(
      `http://${domain}:${port}/socket?channelId=${mySessionId}`,
      null,
      { transports: ["websocket", "xhr-streaming", "xhr-polling"] }
    );
    socket.current.onmessage = onMessage;
    socket.current.onclose = onClose;
    socket.current.onopen = onOpen;

    // --- 1) Get an OpenVidu object ---
    OV = new OpenVidu();
    console.log(OV);

    // --- 2) Init a session ---
    session.current = OV.initSession();
    console.log(session.current);

    // --- 3) Specify the actions when events take place in the session ---
    // On every new Stream received...
    session.current.on("streamCreated", (event) => {
      console.log("세션 온");
      // Subscribe to the Stream to receive it. HTML video will be appended to element with 'video-container' id
      let subscriber = session.current.subscribe(
        event.stream,
        "video-container"
      );

      // When the HTML video has been appended to DOM...
      subscriber.on("videoElementCreated", (event) => {
        console.log("접속자 비디오 생성");
        // Add a new <p> element for the user's nickname just below its video
        appendUserData(event.element, subscriber.stream.connection);
        addVideoInCanvas(event.element, subscriber.stream.connection);
      });
    });

    // On every Stream destroyed...
    session.current.on("streamDestroyed", (event) => {
      console.log("접속자 비디오 나감");
      // Delete the HTML element with the user's nickname. HTML videos are automatically removed from DOM
      removeUserInCanvas(event.stream.connection.connectionId);
      removeUserData(event.stream.connection);
    });

    // On every asynchronous exception...
    session.current.on("exception", (exception) => {
      console.warn(exception);
    });
    // --- 4) Connect to the session with a valid user token ---

    // Get a token from the OpenVidu deployment
    getToken(mySessionId).then((token) => {
      // First param is the token got from the OpenVidu deployment. Second param can be retrieved by every user on event
      // 'streamCreated' (property Stream.connection.data), and will be appended to DOM as the user's nickname
      session.current
        .connect(token, { clientData: myUserName })
        .then(() => {
          // --- 5) Set page layout for active call ---
          document.getElementById("session-title").innerText = mySessionId;
          document.getElementById("join").style.display = "none";
          document.getElementById("session").style.display = "block";

          // --- 6) Get your own camera stream with the desired properties ---
          let publisher = OV.initPublisher("video-container", {
            audioSource: undefined, // The source of audio. If undefined default microphone
            videoSource: undefined, // The source of video. If undefined default webcam
            publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
            publishVideo: true, // Whether you want to start publishing with your video enabled or not
            resolution: "640x480", // The resolution of your video
            frameRate: 30, // The frame rate of your video
            insertMode: "APPEND", // How the video is inserted in the target element 'video-container'
            mirror: false, // Whether to mirror your local video or not
          });
          publisherRef.current = publisher;

          // --- 7) Specify the actions when events take place in our publisher ---
          // When our HTML video has been added to DOM...
          publisher.on("videoElementCreated", function (event) {
            console.log("내 비디오 시작");
            initMainVideo(event.element, myUserName);
            appendUserData(event.element, myUserName);
            addVideoInCanvas(event.element, myUserName);
            event.element["muted"] = true;
          });

          // --- 8) Publish your stream ---
          session.current.publish(publisher);

          let userData = JSON.parse(session.current.connection.data);
          userId = userData.clientData;
        })
        .catch((error) => {
          console.log(
            "There was an error connecting to the session:",
            error.code,
            error.message
          );
        });
    });
  }

  //오픈비두 예제함수
  //세션 나가기를 하면 채널을 나가는 로직이 들어가 있는 함수
  //꼭 필요함
  function leaveSession() {
    // --- 9) Leave the session by calling 'disconnect' method over the Session object ---
    session.current.disconnect();

    // Removing all HTML elements with user's nicknames.
    // HTML videos are automatically removed when leaving a Session
    removeAllUserData();

    //그런데 이 밑은 화상 채팅과 채널명 입력을 바꾸는 곳이라 필요 없음
    // Back to 'Join session' page
    document.getElementById("join").style.display = "block";
    document.getElementById("session").style.display = "none";
  }

  //오픈비두 예제함수
  //창이 꺼지면 소켓을 나가는 함수
  //꼭 필요함
  window.onbeforeunload = function () {
    if (session) session.current.disconnect();
  };

  /* APPLICATION SPECIFIC METHODS */
  //오픈비두 예제 함수
  //페이지가 로드가 되면 기본값으로 채널 명과 랜덤한 유저 아이디를 생성해줌
  //사용 안할듯
  window.addEventListener("load", function () {
    generateParticipantInfo();
  });

  //오픈비두 예제 함수
  //채널 명과 랜덤한 유저 아이디를 생성해줌
  //사용 안할듯
  function generateParticipantInfo() {
    document.getElementById("sessionId").value = "SessionA";
    document.getElementById("userName").value =
      "Participant" + Math.floor(Math.random() * 100);
  }

  //오픈비두 예제 함수
  //현재 참가자 데이터, 영상을 video-container에 넣는다.
  function appendUserData(videoElement, connection) {
    var userData;
    var nodeId;
    if (typeof connection === "string") {
      userData = connection;
      nodeId = connection;
    } else {
      userData = JSON.parse(connection.data).clientData;
      nodeId = connection.connectionId;
    }
    var dataNode = document.createElement("div");
    dataNode.className = "data-node";
    dataNode.id = "data-" + nodeId;
    dataNode.innerHTML = "<p>" + userData + "</p>";
    videoElement.parentNode.insertBefore(dataNode, videoElement.nextSibling);
    addClickListener(videoElement, userData);
  }
  //캔버스에 영상을 삭제하는 함수
  function removeUserInCanvas(connectionId) {
    var dataNode = document.getElementById("data-" + connectionId);
    var targetId = dataNode.innerText;
    //트랜스포머랑 비디오 삭제
    for (var i = 0; i < stage.current.children[1].children.length; i++) {
      console.log(
        stage.current.children[1].children[i].getAttr("id") + " vs " + targetId
      );
      if (stage.current.children[1].children[i].getAttr("id") == targetId) {
        console.log(stage.current.children[1]);
        stage.current.children[1].children[i - 1].remove();
        console.log(stage.current.children[1]);
        stage.current.children[1].children[i - 1].remove();
        break;
      }
    }
    updateCanvasOnlyServer();
  }

  //이용자 나감/튕김으로 인한 캔버스 내용 변경을 서버에 저장하는 함수
  function updateCanvasOnlyServer() {
    let jsonData = {};

    //stage 저장 및 바뀐 객체 전송
    jsonData["BG"] = stage.current.children[0];
    jsonData["video"] = stage.current.children[1];
    jsonData["image"] = stage.current.children[2];
    jsonData["type"] = "update";
    jsonData["channel"] = session.current.sessionId;
    console.log(jsonData);
    console.log(JSON.stringify(jsonData));

    socket.current.send(JSON.stringify(jsonData));
  }

  //캔버스에 영상을 넣는 함수
  function addVideoInCanvas(videoElement, connection) {
    var connectionId = connection;

    //자기 자신의 영상의 커넥션 아이디 : 로그인한 유저 닉네임
    //다른사람의 커넥션 아이디 : 커넥션 고유 번호
    //자기 자신인 경우
    //비디오 영상 레이어 꺼내기
    let layer = stage.current.children[1];

    //비디오 영상 초기 디자인
    var video;

    //자기 자신영상인 경우
    if (typeof connection === "string") {
      connectionId = connection;
    }
    //다른 사람인 경우
    else {
      //다른사람의 로그인 유저 닉네임은 비디오 컨테이너의 data- 중 p태그에 있다
      connectionId = document.querySelector(
        "#data-" + connection.connectionId + " p"
      ).textContent;
    }
    //이미 채널에 참가한 사람의 영상인지 아닌지 비디오 레이어에서 찾는 코드
    let remoteVideo = remoteVideoLayer.find("#" + connectionId);

    //자기 자신인 경우
    if (remoteVideo.length == 0) {
      video = new Konva.Image({
        x: 10,
        y: 10,
        width: 300,
        height: 300,
        image: videoElement,
        draggable: true,
        id: connectionId, //수정하면 안됨!!
        cornerRadius: 150,
      });
    }

    //채녈에 참가한 사람의 영상이 아닌 경우(참가자)
    else {
      video = new Konva.Image({
        x: remoteVideo[0].getAttr("x"),
        y: remoteVideo[0].getAttr("y"),
        width: remoteVideo[0].getAttr("width"),
        height: remoteVideo[0].getAttr("height"),
        image: videoElement,
        draggable: true,
        id: connectionId, //수정하면 안됨!!
        cornerRadius: 150,
      });
    }

    //비디오 크기 및 회전을 도와주는 객체
    var tr = new Konva.Transformer();
    // tr.nodes([video]);

    //비디오 실행
    var animation = new Konva.Animation(function () {}, layer);

    animation.start();

    layer.add(tr);
    layer.add(video);

    //자기 자신의 비디오 경우 자동으로 실행이 되지 않는 오류가 있어 직접 실행
    videoElement.oncanplaythrough = function () {
      videoElement.play();
    };

    //비디오를 움직이면 캔버스 변경사항을 다른사람에게 전송
    video.on("dragend", function () {
      changeCanvas(video);
    });

    //비디오의 모양을 변경하면 캔버스 변경사항을 다른사람에게 전송
    video.on("transformend", function () {
      changeCanvas(video);
    });
  }

  //캔버스의 내용 변경을 감지했을 때 캔버스 데이터를 JSON으로 소켓통신하는 함수
  function changeCanvas(element) {
    console.log("element 변경됨!");
    console.log(element.toJSON());
    let jsonData = {};

    console.log("현재 채널 정보");
    console.log(session.current);
    //stage 저장 및 바뀐 객체 전송
    jsonData["BG"] = stage.current.children[0]; //배경 레이어
    jsonData["video"] = stage.current.children[1]; //비디오 레이어
    jsonData["image"] = stage.current.children[2]; //이미지 레이어
    jsonData["type"] = "change"; //소캣 메시지 타입
    jsonData["object"] = element.toJSON(); //변경된 오브젝트
    jsonData["channel"] = session.current.sessionId; //채널명

    console.log(stage.current.children[0]);
    console.log(stage.current.children[1]);
    console.log(stage.current.children[2]);
    console.log(JSON.stringify(jsonData));
    socket.current.send(JSON.stringify(jsonData)); //소켓 전송
  }

  //오픈비두 기본 함수
  //특정 유저를 지우는 함수
  function removeUserData(connection) {
    var dataNode = document.getElementById("data-" + connection.connectionId);
    dataNode.parentNode.removeChild(dataNode);
  }

  //오픈비두 기본 함수
  //모든 유저 데이터를 지우는 함수
  function removeAllUserData() {
    var nicknameElements = document.getElementsByClassName("data-node");
    while (nicknameElements[0]) {
      nicknameElements[0].parentNode.removeChild(nicknameElements[0]);
    }
  }

  //오픈비두 기본 함수
  //특정 비디오를 클릭하면 해당 비디오가 메인 화면으로 가지는 함수
  //사용 안함
  function addClickListener(videoElement, userData) {
    console.log("add click");
    console.log(videoElement.srcObject);
    videoElement.addEventListener("click", function () {
      var mainVideo = $("#main-video video").get(0);
      if (mainVideo.srcObject !== videoElement.srcObject) {
        $("#main-video").fadeOut("fast", () => {
          $("#main-video p").html(userData);
          mainVideo.srcObject = videoElement.srcObject;
          $("#main-video").fadeIn("fast");
        });
      }
    });
  }
  //오픈비두 기본 함수
  //메인화면 초기화 하는 함수
  //사용 안함
  function initMainVideo(videoElement, userData) {
    document.querySelector("#main-video video").srcObject =
      videoElement.srcObject;
    document.querySelector("#main-video p").innerHTML = userData;
    document.querySelector("#main-video video")["muted"] = true;
  }

  /**
   * --------------------------------------------
   * GETTING A TOKEN FROM YOUR APPLICATION SERVER
   * --------------------------------------------
   * The methods below request the creation of a Session and a Token to
   * your application server. This keeps your OpenVidu deployment secure.
   *
   * In this sample code, there is no user control at all. Anybody could
   * access your application server endpoints! In a real production
   * environment, your application server must identify the user to allow
   * access to the endpoints.
   *
   * Visit https://docs.openvidu.io/en/stable/application-server to learn
   * more about the integration of OpenVidu in your application server.
   */
  //오픈비두 기본 함수
  //최초 접속 시 채팅방에 연결하기 위한 정보(채널, 서버 정보)를 가져오는 함수
  function getToken(mySessionId) {
    return createSession(mySessionId).then((sessionId) =>
      createToken(sessionId)
    );
  }

  //오픈비두 기본 함수
  //최초 접속 시 채팅방을 생성하는 함수
  const createSession = (sessionId) => {
    const url = APPLICATION_SERVER_URL + "api/sessions";
    const headers = { "Content-Type": "application/json" };
    const data = { customSessionId: sessionId };

    return axios
      .post(url, data, { headers })
      .then((response) => response.data)
      .catch((error) => Promise.reject(error));
  };

  //채팅방의 정보를 가져오는 함수
  const createToken = (sessionId) => {
    const url =
      APPLICATION_SERVER_URL + "api/sessions/" + sessionId + "/connections";
    const headers = { "Content-Type": "application/json" };
    const data = {};

    return axios
      .post(url, data, { headers })
      .then((response) => response.data)
      .catch((error) => Promise.reject(error));
  };

  //버튼을 누르면 이미지가 생성되는 함수
  function stickertemp() {
    console.log("click");

    var imageObj = new Image();
    imageObj.src = "https://dm51j1y1p1ekp.cloudfront.net/jjapageti.jpg";
    console.log("이미지 객체");
    console.log(imageObj);
    var papago; //이미지 객체
    let layer;

    //이미지는 바로 로딩이 되지 않기 때문에 이미지가 로딩되면 객체를 생성하는 함수
    imageObj.onload = function () {
      layer = stage.current.children[2];

      //이미지 생성
      papago = new Konva.Image({
        x: 50,
        y: 50,
        image: imageObj,
        width: 106,
        height: 118,
        id: "img-jjapageti.jpg",
        draggable: true,
      });

      var tr = new Konva.Transformer();

      // tr.nodes([papago]);

      //이미지를 움직이면 캔버스 변경사항을 다른사람에게 전송
      papago.on("dragend", function () {
        changeCanvas(papago);
      });

      //이미지의 모양을 변경하면 캔버스 변경사항을 다른사람에게 전송
      papago.on("transformend", function () {
        changeCanvas(papago);
      });

      layer.add(tr);
      layer.add(papago);

      //이미지 생성을 다른 사람들한테도 넘기는 부분
      changeCanvas(papago);
      console.log(papago);
    };
  }

  return (
    <>
      {/* 전체 화면 */}
      <div className="groupchat">
        {/* 캔버스 화면 */}
        <div id="main-container" className="container">
          <div id="join">
            <div id="join-dialog">
              <h1>Join a video session</h1>
              <form className="form-group" onSubmit={joinSession}>
                <p>
                  <label>Participant</label>
                  <input
                    className="form-control"
                    type="text"
                    id="userName"
                    required
                  />
                </p>
                <p>
                  <label>Session</label>
                  <input
                    className="form-control"
                    type="text"
                    id="sessionId"
                    required
                  />
                </p>
                <p className="text-center">
                  <input type="submit" name="commit" value="Join!" />
                </p>
              </form>
            </div>
          </div>

          <div id="channel-screen"></div>
          <div id="shared-screen"></div>
          <button id="button" onClick={stickertemp}>
            파파고 이미지 만들기
          </button>
          <div id="session">
            <div id="session-header">
              <h1 id="session-title"></h1>
              <input
                type="button"
                id="buttonLeaveSession"
                onMouseUp={leaveSession}
                value="Leave session"
              />
            </div>
            <div id="main-video" className="col-md-6">
              <p></p>
              <video autoPlay playsInline={true}></video>
            </div>
            <div id="video-container" className="col-md-6"></div>
          </div>
        </div>
        {/* exit버튼 */}
        <div className="groupchat-exit">
          <Link to="/">
            <img src={exit} alt="" onClick={leaveSession} />
          </Link>
        </div>
        {/* 좌측에서 나올 메뉴들 */}
        <div>
          {/* 세팅 */}
          <div
            className={settingsClicked ? "side-menu-div-on" : "side-menu-div"}
          >
            <ul>
              <div className="accordion">
                <div
                  className={
                    acc_volClicked ? "a-accordion-item" : "accordion-item"
                  }
                >
                  <div className="accordion-header" onClick={acc_volSettings}>
                    참가자 볼륨조절
                  </div>
                  <div className="accordion-content">
                    <div className="accordion-vol">
                      <img src="" alt="아직없어" />
                      <p>김병건</p>
                    </div>
                    <div className="accordion-vol">
                      <img src="" alt="아직없어" />
                      <p>차호민</p>
                    </div>
                    <div className="accordion-vol">
                      <img src="" alt="아직없어" />
                      <p>최춘식</p>
                    </div>
                  </div>
                </div>
              </div>
              <div className="accordion">
                <div className="accordion-item">
                  <div className="accordion-header">내 친구</div>
                  <div className="accordion-content"></div>
                </div>
              </div>
              <div className="accordion">
                <div className="accordion-item">
                  <div className="accordion-header">창작 마당</div>
                  <div className="accordion-content"></div>
                </div>
              </div>
              <div className="accordion">
                <div className="accordion-item">
                  <div className="accordion-header">화면 공유</div>
                  <div className="accordion-content"></div>
                </div>
              </div>
              <div className="accordion">
                <div
                  className={
                    acc_chClicked ? "a-accordion-item" : "accordion-item"
                  }
                >
                  <div className="accordion-header" onClick={acc_chSettings}>
                    채널 이름 변경
                  </div>
                  <div className="accordion-content">
                    <div className="accordion-ch">
                      <input
                        type="text"
                        value={acc_ch_name}
                        onChange={handleInputChange}
                        className="ch-name"
                      />
                      <button onClick={chnameChange} className="ch-name-btn">
                        변경
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              <div className="accordion">
                <div className="accordion-item">
                  <div className="accordion-header">채널 지우기</div>
                  <div className="accordion-content"></div>
                </div>
              </div>
            </ul>
          </div>
          {/* 스티커와 배경화면 */}
          <div
            className={stickerClicked ? "side-menu-div-on" : "side-menu-div"}
          >
            <div id="sticker-and-backimg">
              <button onClick={stickermenuSettings}>스티커</button>
              <p>|</p>
              <button onClick={stickermenuSettings}>배경화면</button>
            </div>
            <ul id={stickermenuClicked ? "stivker-menu-on" : "stivker-menu"}>
              <button onClick={stickertemp}>짜파게티</button>
              <li>스티커도 임시임시</li>
              <li>스티커도 임시임시</li>
              <li>스티커도 임시임시</li>
              <li>스티커도 임시임시</li>
            </ul>
            <ul id={stickermenuClicked ? "back-menu" : "back-menu-on"}>
              <button onClick={() => addBackImageClick(room2)}>회의실</button>
              <button onClick={() => addBackImageClick(pool)}>수영장</button>
              <li>회의실</li>
              <li>수영장</li>
            </ul>
          </div>
          {/* 채팅 */}
          <div className={chatClicked ? "side-menu-div-on" : "side-menu-div"}>
            <ul>
              <li>재미있는 채팅. 지저분한 코드</li>
              <li>재미있는 채팅. 지저분한 코드</li>
              <li>재미있는 채팅. 지저분한 코드</li>
              <li>재미있는 채팅. 지저분한 코드</li>
              <li>재미있는 채팅. 지저분한 코드</li>
              <li>재미있는 채팅. 지저분한 코드</li>
            </ul>
          </div>
        </div>
        {/* 하단 컨트롤 전체 */}
        <div className="underbar-container">
          {/* 좌측 메뉴 버튼 */}
          <div className="left-menu-container">
            {/* 좌측메뉴 숨기기 버튼 */}
            <button>
              <img
                src={back}
                alt=""
                className={backClicked ? "aback" : "back"}
                onClick={backSettings}
              />
            </button>
            {/* 숨겨지는 좌측메뉴들 */}
            <div className={backClicked ? "left-menu-in" : "left-menu-out"}>
              {/* 프로필 */}
              <button
                className="underbar button is-rounded"
                id="profile"
                onClick={profileSettings}
              >
                {/* 프로필 내용물 */}
                <div className="profile-wrapper">
                  <img src={man} alt="" className="user" />
                  <div className="user-stat">
                    <span className="username">이병민</span>
                    <br />
                    <span className="onlinetf">
                      {localStorage.getItem("state")}
                    </span>
                  </div>
                  {/* 프로필 팝오버 */}
                  <div
                    className={
                      profileClicked
                        ? "a-user-stat-control"
                        : "user-stat-control"
                    }
                  >
                    {/* 프로필 팝오버 내용물 */}
                    <label className="radio">
                      <input type="radio" name="answer" onClick={setOnline} />
                      온라인
                    </label>
                    <br />
                    <label className="radio">
                      <input type="radio" name="answer" onClick={setOffline} />
                      오프라인
                    </label>
                  </div>
                </div>
              </button>
              {/* 마이크 조절 버튼 */}
              <button
                className="underbar button is-rounded"
                id="mic"
                onClick={micSettings}
              >
                <img src={micon} alt="" />
                {/* 마이크 조절 팝오버 */}
                <div className={micClicked ? "a-mic-control" : "mic-control"}>
                  마이크 조절을 넣자
                </div>
              </button>
              {/* 볼륨조절 */}
              <button
                className="underbar button is-rounded"
                id="vol"
                onClick={volSettings}
              >
                <img src={volon} alt="" />
                {/* 볼륨조절 팝오버 */}
                <div className={volClicked ? "a-vol-control" : "vol-control"}>
                  볼륨조절을 넣자
                </div>
              </button>
              {/* 카메라 온/오프 */}
              <button
                className="underbar button is-rounded"
                id="cam"
                onClick={camSettings}
              >
                <img src={camClicked ? camoff : camon} alt="" />
              </button>
            </div>
          </div>
          {/* 오른쪽 메뉴들 */}
          <div className="rightmenu">
            {/* 새로운 1대1 메세지 */}
            <button
              className="underbar button is-rounded"
              id={msgClicked ? "new-message" : "a-new-message"}
              onClick={msgSettings}
            >
              <img src={man} alt="" className="user" />
              <div>
                <p>New !</p>
              </div>
            </button>
            {/* 세팅버튼 */}
            <button
              className="underbar"
              onClick={settingsSettings}
              id={settingsClicked ? "asettings" : "settings"}
            >
              <img src={settings} alt="" />
            </button>
            {/* 스티커버튼 */}
            <button
              className="underbar"
              onClick={stickerSettings}
              id={stickerClicked ? "asticker" : "sticker"}
            >
              <img src={sticker} alt="" id="stickerimg" />
            </button>
            {/* 채팅버튼 */}
            <button
              className="underbar"
              onClick={chatSettings}
              id={chatClicked ? "achat" : "chat"}
            >
              <img src={chat} alt="" />
            </button>
          </div>
        </div>
      </div>
    </>
  );
}
