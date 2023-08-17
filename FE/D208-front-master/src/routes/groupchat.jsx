import { useState, useEffect, useRef } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import Konva from "konva";
import { OpenVidu } from "openvidu-browser";
import SockJS from "sockjs-client";
import man from "../assets/man.jpg";
import micon from "../assets/micon.png";
import micoff from "../assets/micoff.png";
import volon from "../assets/volon.png";
import voloff from "../assets/voloff.png";
import camon from "../assets/camon.png";
import camoff from "../assets/camoff.png";
import settings from "../assets/gear.png";
import sticker from "../assets/sticker.png";
import chat from "../assets/chat.png";
import back from "../assets/back.png";
import exit from "../assets/exit.png";
import withview from "../assets/withview.png";
import camera from "../assets/camera.png";
import "../css/groupchat.css";
import StompJs from "stompjs";
import $ from "jquery";
import { useSelector } from "react-redux";
import axiosInstance from "./axiosinstance";
import PresetRegistModal from "./components/presetRegistModal";
import PresetLoadModal from "./components/presetLoadModal";
import StickerContainer from "./components/stickerContainer";

export default function GroupChat() {
  const navigate = useNavigate();
  const windowSize = useRef([window.innerWidth, window.innerHeight]);
  const [lastChat, setLastChat] = useState(null);
  const [backClicked, setbackClicked] = useState(false);
  const [profileClicked, setprofileClicked] = useState(false);
  const [micClicked, setmicClicked] = useState(false);
  const [volClicked, setvolClicked] = useState(false);
  const [camClicked, setcamClicked] = useState(false);
  const [settingsClicked, setsettingsClicked] = useState(false);
  const [presetRegisterClicked, setPresetRegisterClicked] = useState(false);
  const [stickerClicked, setstickerClicked] = useState(false);
  const [stickermenuClicked, setstickermenuClicked] = useState(false);
  const [chatClicked, setchatClicked] = useState(false);
  const [msgClicked, setmsgClicked] = useState(true);
  const [acc_chClicked, setacc_chClicked] = useState(false);
  const [acc_ch_name, setacc_ch_name] = useState();
  const [isCameraOn, setIsCameraOn] = useState(true);
  const [isMicOn, setIsMicOn] = useState(true);
  const [isSpkOn, setIsSpkOn] = useState(true);
  const [subscriber, setSubscriber] = useState(null);
  const [publisher, setPublisher] = useState(null);
  const userSeq = useSelector((state) => state.user.seq);
  const userNick = useSelector((state) => state.user.nickname);
  const userProfile = useSelector((state) => state.user.profile);
  const profileUrl = `https://dm51j1y1p1ekp.cloudfront.net/profile/${userProfile}`;
  // const chatStomp = useSelector((state) => state.stomp);
  const Token = useSelector((state) => state.token);
  const [stickerAndBg, setstickerAndBg] = useState(false);
  const [fullscreen, setFullscreen] = useState(true);
  const [inputText, setInputText] = useState("");
  const [fullsceenChatLog, setFullscreenChatLog] = useState([]);
  const { state } = useLocation();
  const { serverSeq, channelSeq } = state;
  const [presetLoadClicked, setPresetLoadClicked] = useState(false);
  const openFriendListInNewWindow = () => {
    window.open("/friendlist", "_blank", "width=900,height=600");
  };

  function presetLoadClickedChange() {
    setPresetLoadClicked((presetLoadClicked) => !presetLoadClicked);
    console.log(presetLoadClicked);
  }

  function backSettings() {
    setbackClicked((prevbackClicked) => !prevbackClicked);
    setprofileClicked(false);
    setmicClicked(false);
    setvolClicked(false);
  }

  function profileSettings() {
    navigate("/profile");
  }
  const deleteChannel = () => {
    console.log("채널 지우기 버튼 클릭");
    const headers = {
      Authorization: `Bearer ${Token}`,
    };
    if (confirm("정말 채널을 삭제하시겠습니까?")) {
      axiosInstance
        .delete(`/servers/${serverSeq}/channels/${channelSeqRef.current}`, {
          headers,
        })
        .then((response) => {
          alert("채널을 성공적으로 삭제하였습니다.");
          navigate(`/server/${serverSeq}`);
        })
        .catch((err) => {
          alert("채널 삭제를 실패했습니다.");
          console.log("에러 발생");
          console.log(err);
        });
    }
  };
  function micSettings() {
    setmicClicked((prevmicClicked) => !prevmicClicked);
    if (publisher) {
      const audioEnabled = !isMicOn;
      publisher.publishAudio(audioEnabled);
      setIsMicOn(audioEnabled);
    }
    setprofileClicked(false);
  }

  function volSettings() {
    setvolClicked((prevvolClicked) => !prevvolClicked);
    if (subscriber) {
      const audioEnabled = !isSpkOn;
      subscriber.subscribeToAudio(audioEnabled);
      setIsSpkOn(audioEnabled);
    }
    setprofileClicked(false);
  }

  function camSettings() {
    setcamClicked((prevcamClicked) => !prevcamClicked);
    if (camPublisherRef.current) {
      const isCameraOn = !camPublisherRef.current.stream.videoActive;
      camPublisherRef.current.publishVideo(isCameraOn);
      setIsCameraOn(isCameraOn);
      console.log(isCameraOn);

      let myCam; // 변수를 선언한 후, for 루프 내에서 할당
      let myCamID = userNick;
      for (var i = 0; i < stage.current.children[1].children.length; i++) {
        console.log(
          stage.current.children[1].children[i].getAttr("id") + " vs " + myCamID
        );
        if (stage.current.children[1].children[i].getAttr("id") == myCamID) {
          myCam = stage.current.children[1].children[i];
          break;
        }
      }
      if (isCameraOn === true) {
        myCam.setAttr("cornerRadius", 150);
      } else {
        myCam.setAttr("cornerRadius", 149);
      }
      console.log(camPublisherRef.current.stream.streamId);
      console.log(myCam);
      myCam.moveToTop();
      changeCanvas(myCam, "update");
    }
    setprofileClicked(false);
  }

  function presetRegistSettings() {
    setPresetRegisterClicked((presetRegisterClicked) => !presetRegisterClicked);
    console.log(presetRegisterClicked);
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
    setstickerAndBg((prevstickerAndBg) => !prevstickerAndBg);
  }

  function chatSettings() {
    setchatClicked((prevchatClicked) => !prevchatClicked);
    setstickerClicked(false);
    setsettingsClicked(false);
  }
  function msgFalseSettings() {
    setmsgClicked((prevmsgClicked) => false);
  }

  function msgTrueSettings() {
    setmsgClicked((prevmsgClicked) => true);
  }

  function acc_chSettings() {
    setacc_chClicked((prevacc_chClicked) => !prevacc_chClicked);
  }

  const handleInputChange = (event) => {
    setacc_ch_name(event.target.value);
  };

  const chnameChange = () => {
    const headers = {
      Authorization: `Bearer ${Token}`,
    };
    let formData = new FormData();
    formData.append("name", acc_ch_name);

    axiosInstance
      .post(
        `/servers/${serverSeq}/channels/${channelSeqRef.current}`,
        formData,
        { headers }
      )
      .then((response) => {
        alert("채널 이름을 변경하였습니다.");
        console.log(response);
      })
      .catch((err) => {
        alert("채널 이름 변경을 실패하였습니다.");
        console.log(err);
      });
  };

  const fullscreenInputChange = (event) => {
    setInputText(event.target.value);
  };

  const handleTempButtonClick = () => {
    if (window.event.keyCode == 13) {
      setInputText(""); // 입력 후 인풋 초기화
      sendChatSocket(inputText);
    }
  };

  function sidebarChatCss() {
    // 스크롤바를 맨 아래로 내림
    let chatContainer = document.getElementById("chatLogSide");
    chatContainer.scrollTop = chatContainer.scrollHeight;
  }

  function fullscreenChatCss() {
    // 스크롤바를 맨 아래로 내림
    let chatContainer = document.getElementById("chatLogFull");
    chatContainer.scrollTop = chatContainer.scrollHeight;
    setFullscreenChatLog(true);
    setTimeout(() => {
      setFullscreenChatLog(false);
    }, 3000); // 채팅창이 서서히 사라지는 시간
  }

  //konva 오브젝트 아이디 룰
  //이미지 : img-로 시작
  //영상 : 이용자의 아이디

  // let session; //현재 채널 이름(오픈비두에선 채팅방 단위를 'session'이라고 부름)
  let videoContainer = document.querySelector("#video-container"); //오픈비두로 받은 영상을 담은 컨테이너
  // let port = 9091; //백엔드 포트 번호
  // let domain = "http://localhost:9091"; //도메인 주소
  let domain = "https://i9d208.p.ssafy.io"; //도메인 주소;
  let userId = useRef(null); //유저 아이디
  let remoteBGLayer = new Konva.Layer(); //소켓에 저장된 비디오 레이어(최초 접속시 한번 사용)
  let remoteVideoLayer = new Konva.Layer(); //소켓에 저장된 비디오 레이어(최초 접속시 한번 사용)
  let remoteImageLayer = new Konva.Layer(); //소켓에 저장된 이미지 레이어(최초 접속시 한번 사용)
  let stage = useRef(null);
  let stompSocket = useRef(null);
  let stomp = useRef(null);
  let session = useRef(null);
  let sessionScreen = useRef(null);
  let camPublisherRef = useRef(null);
  let screensharing = false;
  let CamOV = useRef(null); //오픈비두 변수
  let ScreenOV = useRef(null); //오픈비두 변수
  // let camCutId = useRef(null);
  let channelSeqRef = useRef(channelSeq);
  let currentShape;

  const handleBackButton = () => {
    if (stomp.current) stomp.current.disconnect();
    console.log("뒤로가기 버튼이 눌렸습니다.");
  };

  const init = () => {
    console.log("첫입장");
    joinSession();

    const container = document.getElementById("channel-screen");
    if (container) {
      stage.current = new Konva.Stage({
        container: "channel-screen",
        x: 0,
        y: 0,
        width: windowSize.current[0],
        height: windowSize.current[1],
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

    // 공유화면 레이어 추가
    const screenLayer = new Konva.Layer();
    stage.current.add(screenLayer);

    // 트랜스포머를 그냥 선언
    let tr = new Konva.Transformer();
    videoLayer.add(tr);
    imageLayer.add(tr);
    screenLayer.add(tr);

    videoLayer.on("click", function (e) {
      // 클릭한 요소를 가져오고 해당 요소를 Transformer에 설정
      let clickedShape = e.target;
      tr.nodes([clickedShape]);

      // 레이어 다시 그리기
      videoLayer.batchDraw();
    });
    screenLayer.on("click", function (e) {
      // 클릭한 요소를 가져오고 해당 요소를 Transformer에 설정
      let clickedShape = e.target;
      tr.nodes([clickedShape]);

      // 레이어 다시 그리기
      screenLayer.batchDraw();
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
  };

  const hideMenu = () => {
    // hide menu
    let deleteBtn = document.querySelector("#delete-button");
    deleteBtn.setAttribute("data-imgid", "");
    let menuNode = document.getElementById("delete-img-menu");
    menuNode.style.display = "none";
  };
  useEffect(() => {
    init();

    return () => {
      leaveSession();
    };
  }, []);

  const resizeWindow = () => {
    stage.current.width(window.innerWidth);
    stage.current.height(window.innerHeight);

    if (
      stage.current === null ||
      stage.current.children[0].children.length == 0
    ) {
      console.log("no stage");
      return;
    }

    stage.current.children[0].children[0].width(window.innerWidth);
    stage.current.children[0].children[0].height(window.innerHeight);

    console.log(stage.current.width);
  };

  useEffect(() => {
    window.addEventListener("resize", resizeWindow);
    window.addEventListener("click", hideMenu);

    return () => {
      window.removeEventListener("resize", resizeWindow);
      window.removeEventListener("click", hideMenu);
    };
  }, [windowSize]);

  // 배경에 이미지 추가
  function addBackImage(imageUrl) {
    console.log("이미지 url");
    console.log(imageUrl);

    const backObj = new Image();
    backObj.crossOrigin = "anonymous";

    backObj.onload = function () {
      const backgroundImage = new Konva.Image({
        id: "bg-" + imageUrl,
        image: backObj,
        width: windowSize.current[0],
        height: windowSize.current[1],
      });

      console.log("이미지 로드!");
      console.log(session.current);

      changeCanvas(backgroundImage, "update");
    };
    backObj.src =
      "https://dm51j1y1p1ekp.cloudfront.net/channel-background/" +
      imageUrl +
      "?timestamp=2";
  }

  const addBackImageClick = (imageUrl) => {
    console.log("배경 이미지 변환");
    console.log(session.current);

    addBackImage(imageUrl);
  };

  //켄버스에 변경 내용을 저장하는 함수
  function loadCanvasChange(data) {
    console.log(data);
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
        //이미지 변수 생성
        let imageObj = new Image();
        imageObj.crossOrigin = "anonymous";

        // let splitedImageId = objectId.split("-");
        console.log(
          objectId.substring(objectId.indexOf("-", objectId.indexOf("-")) + 1)
        );
        let imageName = objectId.substring(
          objectId.indexOf("-", objectId.indexOf("-") + 1) + 1
        ); //img- 제거한 나머지를 이름으로 설정
        imageObj.src =
          "https://dm51j1y1p1ekp.cloudfront.net/sticker/" +
          imageName +
          "?timestamp=2";

        //로딩돠면
        imageObj.onload = function () {
          object.setAttr("image", imageObj);
          object.setAttr("width", 100);
          object.setAttr("height", 100);
        };

        //드래그 반응이 끝나면 캔버스 넘기기
        object.on("dragend", function () {
          changeCanvas(object, "update");
        });

        //비디오의 모양을 변경하면 캔버스 변경사항을 다른사람에게 전송
        object.on("transformend", function () {
          changeCanvas(object, "update");
        });
        var menuNode = document.getElementById("delete-img-menu");

        // 우클릭 이벤트 핸들러 등록
        object.on("contextmenu", function (e) {
          e.evt.preventDefault();
          console.log("우클릭 입력");
          if (e.target === stage.current) {
            // if we are on empty place of the stage we will do nothing
            return;
          }

          let deleteBtn = document.querySelector("#delete-button");
          deleteBtn.setAttribute("data-imgid", object.getAttr("id"));

          currentShape = e.target;
          menuNode.style.display = "initial";
          menuNode.style.zIndex = "1";
          console.log(menuNode);
          var containerRect = stage.current.container().getBoundingClientRect();
          menuNode.style.top =
            containerRect.top + stage.current.getPointerPosition().y + 4 + "px";
          menuNode.style.left =
            containerRect.left +
            stage.current.getPointerPosition().x +
            4 +
            "px";
        });
        stage.current.children[2].add(object);
      }

      // 배경 삽입
      else if (objectId.indexOf("bg-") != -1) {
        stage.current.children[0].removeChildren();
        //배경 변수 생성
        let backObj = new Image();
        backObj.crossOrigin = "anonymous";
        let imageName = objectId.substring(3); // 임시 어차피 바꿔야함

        console.log("배경 이미지 이름");
        console.log(imageName);

        backObj.src =
          "https://dm51j1y1p1ekp.cloudfront.net/channel-background/" +
          imageName +
          "?timestamp=2";

        //로딩돠면
        backObj.onload = function () {
          object.setAttr("image", backObj);
          object.setAttr("width", windowSize.current[0]);
          object.setAttr("height", windowSize.current[1]);
        };

        stage.current.children[0].add(object);
      }
    } else {
      //이미지 위치 변경
      if (objectId.indexOf("img-") != -1) {
        target[0].setAttrs(object.getAttrs());
      }
      //공유화면 위치 변경
      else if (objectId.indexOf("screen-") != -1) {
        target[0].setAttrs(object.getAttrs());
      }
      //영상 위치 변경
      else {
        target[0].setAttrs(object.getAttrs());
        // let triger = target[0].getAttr("cornerRadius");
        // if (triger === 149) {
        //   const tempvideoElement = target[0].attrs.image;
        //   camCutId.current = tempvideoElement.getAttribute("id");
        //   console.log(camCutId.current);
        //   console.log("캠꺼진상태");
        //   const profileElement = new Image();
        //   profileElement.src = withview;
        //   target[0].image(profileElement);
        // } else {
        //   console.log("캠켜진상태");
        //   if (objectId != userNick) {
        //     console.log("남의화면");
        //     target[0].image(document.getElementById(camCutId.current));
        //   } else {
        //     console.log("내화면");
        //     target[0].image(document.getElementById("local-video-undefined"));
        //   }
        // }
      }
    }
  }

  function onConnected() {
    console.log("소켓 연결 성공");

    stomp.current.subscribe(
      "/api/sub/canvas/channel/" + channelSeqRef.current,
      function (msg) {
        console.log("캔버스 메세지 도착!");
        console.log(msg);

        let data = JSON.parse(msg.body);
        console.log(data.type);

        if (data.type == "update") {
          console.log("세션");
          console.log(session.current);
          console.log("캔버스 변화");
          console.log(data);
          loadCanvasChange(data.object);
        } else if (data.type == "delete") {
          console.log("요소 삭제");
          console.log(data);
          deleteCanvasChange(data.object);
        } else if (data.type == "load") {
          console.log("프리셋 불러오기");
          stage.current.children[0].removeChildren();
          stage.current.children[2].removeChildren();
          loadCanvas(data);
        } else {
          console.log("데이터");
          console.log(data);
        }
      }
    );
    chatConnect();
    chatEnterMsg();
  }

  function deleteCanvasChange(data) {
    let deleteElement = Konva.Node.create(data);

    console.log("삭제 객체");
    console.log(deleteElement);
    let objectId = deleteElement.attrs.id;
    console.log("소캣에서 넘어온 객체 아이디");
    console.log(objectId);
    console.log(stage.current);

    let target = stage.current.find("#" + objectId); //객체 탐색
    if (target.length > 0) {
      target[0].remove();
    }
  }

  function onError() {
    console.log("소켓 연결 실패");
  }
  //오픈비두 기본함수
  //채널에 들어오면 sessionId와 userName(유저 아이디)를 통해 채널에 들어가는 로직이 들어가 있는 함수
  //제일 중요함
  /* OPENVIDU METHODS */
  function loadCanvas(data) {
    let backChildren = JSON.parse(data.background);

    if (backChildren.children.length > 0) {
      console.log("서버에 있는 배경 레이어");
      remoteBGLayer = Konva.Node.create(data.background);
      console.log(remoteBGLayer);

      let backgroundObj = remoteBGLayer.children[0];
      console.log(backgroundObj);
      //이미지 변수 생성
      let imageObj = new Image();
      imageObj.crossOrigin = "anonymous";
      let imageName = backgroundObj.getAttr("id"); //img- 제거한 나머지를 이름으로 설정
      console.log(imageName);
      imageObj.src =
        "https://dm51j1y1p1ekp.cloudfront.net/channel-background/" +
        imageName.substring(3) +
        "?timestamp=2";

      //로딩돠면
      imageObj.onload = function () {
        backgroundObj.setAttr("image", imageObj);
        backgroundObj.setAttr("width", windowSize.current[0]);
        backgroundObj.setAttr("height", windowSize.current[1]);
      };

      console.log(stage.current);

      stage.current.children[0].add(backgroundObj);
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
        //이미지 변수 생성
        let imageObj = new Image();
        imageObj.crossOrigin = "anonymous";
        let splitedName = images[i].getAttr("id").split("-");
        let imageName = images[i]
          .getAttr("id")
          .substring(
            images[i]
              .getAttr("id")
              .indexOf("-", images[i].getAttr("id").indexOf("-") + 1) + 1
          ); //img- 제거한 나머지를 이름으로 설정
        console.log("최초 이미지 로드 대상");

        console.log(imageName);
        imageObj.src =
          "https://dm51j1y1p1ekp.cloudfront.net/sticker/" +
          imageName +
          "?timestamp=2";

        console.log("이미지 경로");
        console.log(imageObj.src);
        //로딩돠면
        imageObj.onload = function () {
          console.log("이미지 로드 완료");
          images[i].setAttr("image", imageObj);
        };

        //드래그 반응이 끝나면 캔버스 넘기기
        images[i].on("dragend", function () {
          changeCanvas(images[i], "update");
        });

        //비디오의 모양을 변경하면 캔버스 변경사항을 다른사람에게 전송
        images[i].on("transformend", function () {
          changeCanvas(images[i], "update");
        });
        console.log(stage.current);

        function limitDragBounds(pos) {
          var newX = Math.max(
            0,
            Math.min(stage.current.width() - images[i].width(), pos.x)
          );
          var newY = Math.max(
            0,
            Math.min(stage.current.height() - images[i].height(), pos.y)
          );
          return { x: newX, y: newY };
        }

        // 드래그 범위 제한 함수를 객체에 연결
        images[i].dragBoundFunc(limitDragBounds);
        var menuNode = document.getElementById("delete-img-menu");

        // 우클릭 이벤트 핸들러 등록
        images[i].on("contextmenu", function (e) {
          e.evt.preventDefault();
          console.log("우클릭 입력");

          let deleteBtn = document.querySelector("#delete-button");
          deleteBtn.setAttribute("data-imgid", images[i].getAttr("id"));
          if (e.target === stage.current) {
            return;
          }

          currentShape = e.target;
          menuNode.style.display = "initial";
          menuNode.style.zIndex = "1";

          console.log(menuNode);

          var containerRect = stage.current.container().getBoundingClientRect();
          menuNode.style.top =
            containerRect.top + stage.current.getPointerPosition().y + 4 + "px";
          menuNode.style.left =
            containerRect.left +
            stage.current.getPointerPosition().x +
            4 +
            "px";
        });
        stage.current.children[2].add(images[i]);
      }
    }
  }

  async function joinSession() {
    console.log("join !");
    if (stompSocket.current != null) {
      return;
    }

    let mySessionId = "channel" + "_" + channelSeqRef.current;
    userId = userNick;

    //참가한 채널 명을 url로 구분하도록 커스터마이징함
    console.log("캔버스 로드");
    const headers = {
      Authorization: `Bearer ${Token}`,
      "Content-Type": "application/json",
    };

    let apiCall = await axiosInstance
      .get(`/canvas/${channelSeqRef.current}`, { headers })
      .then((response) => {
        console.log("받은 데이터");
        console.log(response);
        let data = response.data.canvas;
        if (data == null) {
          let parameters = {
            channelSeq: channelSeqRef.current,
            background: stage.current.children[0],
            image: stage.current.children[1],
            video: stage.current.children[2],
          };
          axiosInstance
            .post(`/canvas`, parameters, { headers })
            .then((response) => {
              console.log(response);
            })
            .catch((err) => {
              console.log(err);
            });
        } else {
          loadCanvas(data);
        }
      })
      .catch((err) => {
        console.log("에러 발생");
        console.log(err);
      });

    stompSocket.current = new SockJS(`${domain}/api/ws-stomp`);
    stomp.current = StompJs.over(stompSocket.current);
    stomp.current.connect({ userSeq: userSeq }, onConnected, onError);

    // --- 1) Get an OpenVidu object ---
    CamOV.current = new OpenVidu();
    ScreenOV.current = new OpenVidu();

    // --- 2) Init a session ---
    session.current = CamOV.current.initSession();
    sessionScreen.current = ScreenOV.current.initSession();

    //말하면 반응하는거
    session.current.on("publisherStartSpeaking", (event) => {
      console.log(event);
      let speakUserId = JSON.parse(event.connection.data).clientData;
      let videoId;

      if (speakUserId == userId) {
        videoId = "local-video-undefined";
      } else {
        videoId = "remote-video-" + event.streamId;
      }

      console.log(speakUserId + "가 말하는 중");
      console.log("비디오 아이디 : " + videoId);
      console.log("비디오가 들어있는 div");
      console.log(document.querySelector("#" + videoId));

      const videoBox = document.getElementById("video-container");
      const originalVideo = videoBox.querySelector("#" + videoId);
      originalVideo.setAttribute("height", 150);
      originalVideo.setAttribute("width", 200);

      const destinationContainer = document.getElementById("speakingdiv");
      destinationContainer.appendChild(originalVideo);

      const nameTag = document.createElement("div");
      nameTag.textContent = speakUserId;
      nameTag.classList.add(speakUserId);
      const destinationNameContainer = document.getElementById("speakingName");
      destinationNameContainer.appendChild(nameTag);
    });

    //말 끝나면 반응하는거
    session.current.on("publisherStopSpeaking", (event) => {
      console.log(event);
      let speakUserId = JSON.parse(event.connection.data).clientData;
      let videoId;

      if (speakUserId == userId) {
        videoId = "local-video-undefined";
      } else {
        videoId = "remote-video-" + event.streamId;
      }

      const destinationContainer = document.getElementById("speakingdiv");
      const clonedVideo = destinationContainer.querySelector("#" + videoId);
      const destinationNameContainer = document.getElementById("speakingName");
      const nameTag = destinationNameContainer.querySelector("." + speakUserId);
      destinationNameContainer.removeChild(nameTag);

      const videoBox = document.getElementById("video-container");
      videoBox.appendChild(clonedVideo);

      console.log(speakUserId + "가 말 끝남");
    });

    // --- 3) Specify the actions when events take place in the session ---
    // On every new Stream received...
    session.current.on("streamCreated", (event) => {
      console.log("카메라 세션 온");
      // Subscribe to the Stream to receive it. HTML video will be appended to element with 'video-container' id
      if (event.stream.typeOfVideo == "CAMERA") {
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
        setSubscriber(subscriber);
      }
    });

    sessionScreen.current.on("streamCreated", (event) => {
      if (event.stream.typeOfVideo == "SCREEN") {
        // Subscribe to the Stream to receive it. HTML video will be appended to element with 'container-screens' id
        var subscriberScreen = sessionScreen.current.subscribe(
          event.stream,
          "container-screens"
        );
        // When the HTML video has been appended to DOM...
        subscriberScreen.on("videoElementCreated", (event) => {
          // Add a new <p> element for the user's nickname just below its video
          appendShareData(event.element, subscriberScreen.stream.connection);
          addScreenInCanvas(event.element, subscriberScreen.stream.connection);
        });
      }
    });
    // On every Stream destroyed...
    session.current.on("streamDestroyed", (event) => {
      // if (event.reason != "unpublish") {
      console.log(session.current);
      console.log(sessionScreen.current);
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
        .connect(token, { clientData: userId })
        .then(() => {
          // --- 5) Set page layout for active call ---
          document.getElementById("session-title").innerText = mySessionId;
          document.getElementById("join").style.display = "none";
          document.getElementById("session").style.display = "block";

          // --- 6) Get your own camera stream with the desired properties ---
          let publisher = CamOV.current.initPublisher("video-container", {
            audioSource: undefined, // The source of audio. If undefined default microphone
            videoSource: undefined, // The source of video. If undefined default webcam
            publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
            publishVideo: true, // Whether you want to start publishing with your video enabled or not
            resolution: "640x480", // The resolution of your video
            frameRate: 30, // The frame rate of your video
            insertMode: "APPEND", // How the video is inserted in the target element 'video-container'
            mirror: false, // Whether to mirror your local video or not
          });
          console.log("cam");
          console.log(publisher);
          camPublisherRef.current = publisher;
          setPublisher(publisher);

          // --- 7) Specify the actions when events take place in our publisher ---
          // When our HTML video has been added to DOM...
          publisher.on("videoElementCreated", function (event) {
            console.log("내 비디오 시작");
            initMainVideo(event.element, userId);
            appendUserData(event.element, userId);
            addVideoInCanvas(event.element, userId);
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

    getToken(mySessionId).then((tokenScreen) => {
      // Create a token for screen share
      sessionScreen.current
        .connect(tokenScreen, { clientData: userId })
        .then(() => {
          console.log("Session screen connected");
        })
        .catch((error) => {
          console.warn(
            "There was an error connecting to the session for screen share:",
            error.code,
            error.message
          );
        });
    });
    console.log(stomp.current);
  }

  function chatConnect() {
    console.log(channelSeqRef.current);
    console.log("connect CHAT");
    console.log(stomp.current);
    const channelSubscribe = stomp.current.subscribe(
      `/api/sub/chat/channel/${channelSeqRef.current}`,
      (message) => {
        console.log("구독 성공");
        const receivedMessage = JSON.parse(message.body);
        console.log(receivedMessage);
        setLastChat(receivedMessage);

        if (!chatClicked && receivedMessage.userDto.nickname != userNick) {
          setmsgClicked(false);
        }
        recvMessage(receivedMessage); // 채팅내용을 처리하는 함수 호출

        // 채팅창 맨 밑으로
        sidebarChatCss();
        fullscreenChatCss();
      }
    );
    stomp.current.send(
      `/api/pub/chat/channel/message`,
      {},
      JSON.stringify({
        channelSeq: channelSeqRef.current,
        userSeq: userSeq,
        message: userNick + "님이 참여하셨습니다 !",
      })
    );

    return () => {
      console.log("채널 나감");
      console.log(stomp);
      channelSubscribe.unsubscribe(
        `/api/sub/chat/channel/${channelSeqRef.current}`,
        function () {
          stomp.send(
            `/api/pub/server/${serverSeq}/channel/${channelSeqRef.current}/leave`,
            {},
            JSON.stringify({ userSeq: { userSeq } })
          );
        }
      );
      stomp.current.disconnect(); // 컴포넌트 언마운트 시 연결 해제
    };
  }

  function chatEnterMsg() {
    console.log("들어가는 메세지를 보내주자 !");
    stomp.current.send(
      `/api/pub/server/${serverSeq}/channel/${channelSeqRef.current}/enter`,
      {},
      JSON.stringify({ userSeq: userSeq })
    );
  }

  function chatLeaveMsg() {
    console.log("나가는 메세지를 보내주자 !");
    stomp.current.send(
      `/api/pub/server/${serverSeq}/channel/${channelSeqRef.current}/leave`,
      {},
      JSON.stringify({ userSeq: userSeq })
    );
  }

  //외부를 클릭하면 우클릭 메뉴가 없어지는 것
  // window.addEventListener("click", () => {
  //   if (location.pathname.indexOf("/groupchat") == -1) {
  //     return;
  //   }
  //   // hide menu
  //   let deleteBtn = document.querySelector("#delete-button");
  //   deleteBtn.setAttribute("data-imgid", "");
  //   let menuNode = document.getElementById("delete-img-menu");
  //   menuNode.style.display = "none";
  // });

  function publishScreenShare() {
    // --- 9.1) To create a publisherScreen set the property 'videoSource' to 'screen'
    console.log("스크린 ov");
    console.log(stage);
    console.log(ScreenOV.current);
    if (ScreenOV.current != null && ScreenOV.current.publishers.length > 0) {
      for (let i = 0; i < ScreenOV.current.publishers.length; i++) {
        console.log("기존에 존재한 스트림");
        console.log(ScreenOV.current.publishers[i]);
        sessionScreen.current.unpublish(ScreenOV.current.publishers[i]);
      }
    }
    var publisherScreen = ScreenOV.current.initPublisher("container-screens", {
      videoSource: "screen",
    });

    console.log("스크린 publisher");
    console.log(publisherScreen);

    // --- 9.2) Publish the screen share stream only after the user grants permission to the browser
    publisherScreen.once("accessAllowed", () => {
      screensharing = true;
      // If the user closes the shared window or stops sharing it, unpublish the stream
      publisherScreen.stream
        .getMediaStream()
        .getVideoTracks()[0]
        .addEventListener("ended", () => {
          console.log('User pressed the "Stop sharing" button');
          sessionScreen.current.unpublish(publisherScreen);
          screensharing = false;
          setFullscreen(true);
        });
      sessionScreen.current.publish(publisherScreen);
    });

    publisherScreen.on("videoElementCreated", function (event) {
      console.log("videoElementCreated");
      appendShareData(event.element, sessionScreen.current.connection);
      addScreenInCanvas(event.element, sessionScreen.current.connection);
      event.element["muted"] = true;
    });

    publisherScreen.once("accessDenied", () => {
      console.error("Screen Share: Access Denied");
    });
  }

  //오픈비두 예제함수
  //세션 나가기를 하면 채널을 나가는 로직이 들어가 있는 함수
  //꼭 필요함
  function leaveSession() {
    // --- 9) Leave the session by calling 'disconnect' method over the Session object ---
    // sessionScreen.current.disconnect();
    session.current.disconnect();
    sessionScreen.current.disconnect();
    if (stomp.current) stomp.current.disconnect();
    chatLeaveMsg();
    // Removing all HTML elements with user's nicknames.
    // HTML videos are automatically removed when leaving a Session

    removeAllUserData();
    screensharing = false;
  }

  //오픈비두 예제함수
  //창이 꺼지면 소켓을 나가는 함수
  //꼭 필요함
  window.onbeforeunload = function () {
    if (session) session.current.disconnect();
    if (session) sessionScreen.current.disconnect();
    if (stomp.current) stomp.current.disconnect();
  };

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
    dataNode.crossOrigin = "anonymous";

    dataNode.innerHTML = "<p>" + userData + "</p>";
    videoElement.parentNode.insertBefore(dataNode, videoElement.nextSibling);
    addClickListener(videoElement, userData);
    console.log("data clear");
  }

  //오픈비두 예제 함수
  //현재 참가자 데이터, 영상을 video-container에 넣는다.
  function appendShareData(videoElement, connection) {
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
    dataNode.innerHTML = "<p>" + userData + "-share" + "</p>";
    videoElement.parentNode.insertBefore(dataNode, videoElement.nextSibling);
    addClickListener(videoElement, userData);
    console.log("data clear");
  }

  //캔버스에 영상을 삭제하는 함수
  function removeUserInCanvas(connectionId) {
    console.log("영상 아이디");
    console.log(connectionId);
    var dataNode = document.getElementById("data-" + connectionId);
    var targetId = dataNode.innerText;
    console.log(targetId);
    //트랜스포머랑 비디오 삭제
    let target = stage.current.find("#" + targetId);
    if (target.length > 0) {
      target[0].remove();
    }
    updateCanvasOnlyServer();
  }
  function sendStageInfo(stage) {
    let data = Konva.Node.create(stage, "remote-container");

    let jsonData = {};
    //stage 저장 및 바뀐 객체 전송
    jsonData["background"] = data.children[0];
    jsonData["video"] = data.children[1];
    jsonData["image"] = data.children[2];
    jsonData["type"] = "load";
    jsonData["channelSeq"] = channelSeqRef.current;
    jsonData["userSeq"] = userSeq;
    jsonData["stage"] = stage;

    console.log(jsonData);
    console.log(JSON.stringify(jsonData));

    stomp.current.send(
      "/api/pub/canvas/channel/" + channelSeqRef.current,
      {},
      JSON.stringify(jsonData)
    );

    document.querySelector("#remote-container").innerHTML = "";
  }

  //이용자 나감/튕김으로 인한 캔버스 내용 변경을 서버에 저장하는 함수
  function updateCanvasOnlyServer() {
    let stompData = {};
    stompData["background"] = stage.current.children[0];
    stompData["video"] = stage.current.children[1];
    stompData["image"] = stage.current.children[2];
    stompData["type"] = "update";
    stompData["channelSeq"] = channelSeqRef.current;
    stompData["userSeq"] = userSeq;
    stompData["stage"] = stage.current;

    stomp.current.send(
      "/api/pub/canvas/channel/" + channelSeqRef.current,
      {},
      JSON.stringify(stompData)
    );
  }

  //캔버스에 영상을 넣는 함수
  function addVideoInCanvas(videoElement, connection) {
    console.log("video in canvas");
    var connectionId = connection;

    //자기 자신의 영상의 커넥션 아이디 : 로그인한 유저 닉네임
    //다른사람의 커넥션 아이디 : 커넥션 고유 번호
    //자기 자신인 경우
    //비디오 영상 레이어 꺼내기
    let layer = stage.current.children[1];

    //비디오 영상 초기 디자인
    var video;
    // var baseImg;
    console.log("connectionId");
    console.log(connectionId);
    console.log("layer");
    console.log(layer);

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
      console.log(connectionId);
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
        visible: true,
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
        visible: true,
      });
    }
    //비디오 실행
    var animation = new Konva.Animation(function () {}, layer);

    animation.start();

    // 드래그 범위 제한 함수 정의
    function limitDragBounds(pos) {
      var newX = Math.max(
        0,
        Math.min(stage.current.width() - video.width(), pos.x)
      );
      var newY = Math.max(
        0,
        Math.min(stage.current.height() - video.height(), pos.y)
      );
      return { x: newX, y: newY };
    }

    // 드래그 범위 제한 함수를 객체에 연결
    video.dragBoundFunc(limitDragBounds);
    layer.add(video);
    console.log("점프");
    console.log(video);

    //자기 자신의 비디오 경우 자동으로 실행이 되지 않는 오류가 있어 직접 실행
    videoElement.oncanplaythrough = function () {
      videoElement.play();
      console.log("한번더 점프");
    };
    console.log("한번더 점프");

    //비디오를 움직이면 캔버스 변경사항을 다른사람에게 전송
    video.on("dragend", function () {
      changeCanvas(video, "update");
    });

    //비디오의 모양을 변경하면 캔버스 변경사항을 다른사람에게 전송
    video.on("transformend", function () {
      changeCanvas(video, "update");
    });
  }

  //캔버스의 내용 변경을 감지했을 때 캔버스 데이터를 JSON으로 소켓통신하는 함수
  function changeCanvas(element, type) {
    let stompData = {};
    stompData["background"] = stage.current.children[0];
    stompData["video"] = stage.current.children[1];
    stompData["image"] = stage.current.children[2];
    stompData["type"] = type;
    stompData["channelSeq"] = channelSeqRef.current;
    stompData["userSeq"] = userSeq;
    stompData["object"] = element.toJSON();
    stompData["stage"] = stage.current;

    stomp.current.send(
      "/api/pub/canvas/channel/" + channelSeqRef.current,
      {},
      JSON.stringify(stompData)
    );
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
  //최초 접속 시 채팅방에 연결하기 위한 정보(채널, 서버 정보)를 가져오는 함수
  function getToken(mySessionId) {
    console.log("토큰 받기");
    return createSession(mySessionId).then((sessionId) =>
      createToken(sessionId)
    );
  }

  //오픈비두 기본 함수
  //최초 접속 시 채팅방을 생성하는 함수
  const createSession = (sessionId) => {
    const url = "/sessions";
    const headers = {
      Authorization: `Bearer ${Token}`,
      "Content-Type": "application/json",
    };
    const data = {
      customSessionId: sessionId,
      channelSeq: channelSeqRef.current,
    };
    console.log("세션 만들기");
    return axiosInstance
      .post(url, data, { headers })
      .then((response) => {
        console.log(response);
        return response.data;
      })
      .catch((error) => Promise.reject(error));
  };

  //채팅방의 정보를 가져오는 함수
  const createToken = (sessionId) => {
    const url = "/sessions/" + sessionId + "/connections";
    const headers = {
      Authorization: `Bearer ${Token}`,
      "Content-Type": "application/json",
    };
    const data = {};

    return axiosInstance
      .post(url, data, { headers })
      .then((response) => response.data)
      .catch((error) => Promise.reject(error));
  };

  function addClickListener(videoElement, userData) {
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

  function initMainVideo(videoElement, userData) {
    document.querySelector("#main-video video").srcObject =
      videoElement.srcObject;
    document.querySelector("#main-video p").innerHTML = userData;
    document.querySelector("#main-video video")["muted"] = true;
  }

  function deleteClickListener() {
    //삭제 버튼 누를시 이벤트
    let deleteBtn = document.querySelector("#delete-button");
    console.log(deleteBtn);
    let targetId = deleteBtn.getAttribute("data-imgid");
    console.log("삭제 아이디");
    console.log(targetId);

    let target = stage.current.find("#" + targetId);
    if (target.length > 0) {
      changeCanvas(target[0], "delete");
    }
  }
  //버튼을 누르면 이미지가 생성되는 함수
  function stickertemp(name) {
    console.log("click");
    var imageObj = new Image();
    imageObj.crossOrigin = "anonymous";

    imageObj.src =
      `https://dm51j1y1p1ekp.cloudfront.net/sticker/${name}` + "?timestamp=2";
    console.log("이미지 객체");
    console.log(imageObj);

    var papago; //이미지 객체
    let layer;

    //이미지는 바로 로딩이 되지 않기 때문에 이미지가 로딩되면 객체를 생성하는 함수
    imageObj.onload = function () {
      let seq = 1;
      let existImg = stage.current.find(`#img-${seq}-${name}`);

      console.log(`img-${seq}-${name}` + " 이미 존재하는 이미지");
      console.log(existImg);

      //이미지 고유 seq값 넣기
      while (existImg.length > 0) {
        console.log("이미 존재하는 이미지");
        console.log(existImg);

        seq = seq + 1;
        existImg = stage.current.find(`#img-${seq}-${name}`);
      }

      //이미지 생성
      papago = new Konva.Image({
        x: 50,
        y: 50,
        image: imageObj,
        width: 106,
        height: 118,
        id: `img-${seq}-${name}`,
        draggable: true,
        visible: true,
      });

      changeCanvas(papago, "update");
    };
  }
  // 공유화면 캔버스에 그리기
  function addScreenInCanvas(videoElement, connection) {
    console.log("share video in canvas");
    var connectionId = connection;
    console.log("전송받은 커넥션");
    console.log(connection);

    //자기 자신의 영상의 커넥션 아이디 : 로그인한 유저 닉네임
    //다른사람의 커넥션 아이디 : 커넥션 고유 번호
    //자기 자신인 경우
    //공유 영상 레이어 꺼내기
    let layer = stage.current.children[3];

    //공유 영상 초기 디자인
    var video;
    console.log("connectionId");
    console.log(connectionId);
    console.log("layer");
    console.log(layer);

    //자기 자신영상인 경우
    if (typeof connection === "string") {
      connectionId = "screen-" + connection;
    }
    //다른 사람인 경우
    else {
      //다른사람의 로그인 유저 닉네임은 비디오 컨테이너의 data- 중 p태그에 있다
      connectionId = document.querySelector(
        "#data-" + connection.connectionId + " p"
      ).textContent;
      console.log(connectionId);
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
        id: "screen-" + connectionId, //수정하면 안됨!!
        visible: true,
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
        id: "screen-" + connectionId, //수정하면 안됨!!
        visible: true,
      });
    }

    //비디오 실행
    var animation = new Konva.Animation(function () {}, layer);

    animation.start();

    // 드래그 범위 제한 함수 정의
    function limitDragBounds(pos) {
      var newX = Math.max(
        0,
        Math.min(stage.current.width() - video.width(), pos.x)
      );
      var newY = Math.max(
        0,
        Math.min(stage.current.height() - video.height(), pos.y)
      );
      return { x: newX, y: newY };
    }

    // 드래그 범위 제한 함수를 객체에 연결
    video.dragBoundFunc(limitDragBounds);
    layer.add(video);

    //자기 자신의 비디오 경우 자동으로 실행이 되지 않는 오류가 있어 직접 실행
    videoElement.oncanplaythrough = function () {
      videoElement.play();
    };

    //비디오를 움직이면 캔버스 변경사항을 다른사람에게 전송
    video.on("dragend", function () {
      changeCanvas(video, "update");
    });

    //비디오의 모양을 변경하면 캔버스 변경사항을 다른사람에게 전송
    video.on("transformend", function () {
      changeCanvas(video, "update");
    });

    video.on("contextmenu", function (e) {
      console.log(e.target);
      e.evt.preventDefault();
      // video.setAttrs({
      //   x: -5,
      //   y: -5,
      //   draggable: false,
      //   width: window.innerWidth + 10,
      //   height: window.innerHeight + 10,
      // });
      // layer.draw();
      // console.log(video.attrs);
      // console.log(layer);
      const shaerVideoId = e.target.attrs.image.id;
      const sharingVideoDiv = document.getElementById("container-screens");
      const targetVideo = sharingVideoDiv.querySelector("#" + shaerVideoId);

      const destinationContainer = document.getElementById("bigfulldiv");
      destinationContainer.appendChild(targetVideo);
      setFullscreen(false);
      setsettingsClicked(false);
      setstickerClicked(false);
      setchatClicked(false);
    });
  }
  function pushX() {
    // let targerlayer = stage.current.children[3];
    // let sharescreen = targerlayer.find((node) => node.id().endsWith("-share"));
    // if (sharescreen[0]) {
    //   console.log(sharescreen[0]);
    //   console.log(sharescreen[0].attrs);
    //   sharescreen[0].setAttrs({
    //     x: 10,
    //     y: 10,
    //     width: 300,
    //     height: 300,
    //     draggable: true,
    //   });
    //   targerlayer.draw();
    // }
    const destinationContainer = document.getElementById("bigfulldiv");
    const targetVideo = destinationContainer.querySelector("video");
    const videoBox = document.getElementById("container-screens");
    videoBox.appendChild(targetVideo);
    setFullscreen(true);
  }

  const divRef = useRef(null);
  useEffect(() => {
    const observerCallback = (mutationsList) => {
      for (const mutation of mutationsList) {
        if (mutation.type === "childList") {
          console.log("Child nodes have changed:", mutation.addedNodes);
          if (mutation.addedNodes.length === 0) {
            setFullscreen(true);
          }
        }
      }
    };

    const observerOptions = {
      childList: true,
    };

    const observer = new MutationObserver(observerCallback);

    if (divRef.current) {
      observer.observe(divRef.current, observerOptions);
    }

    return () => {
      if (divRef.current) {
        observer.disconnect();
      }
    };
  }, []);

  function sendChatSocket(message) {
    // 웹소켓에 채팅 전송하는 부분
    console.log(stomp.current);
    console.log(message);
    stomp.current.send(
      "/api/pub/chat/channel/message",
      {},
      JSON.stringify({
        userSeq: userSeq,
        channelSeq: channelSeqRef.current,
        message: message,
      })
    );
  }

  function recvMessage(recieve) {
    console.log("채팅 날라옴");
    console.log(recieve);
    let chatMessage = recieve.message;
    const chatOwner = recieve.userDto.nickname;
    const chatTime = recieve.sendTime.substr(11, 5);
    const chatImage = recieve.userDto.profileImgSearchName;
    const chatLogSide = document.getElementById("chatLogSide");
    const chatLogFull = document.getElementById("chatLogFull");
    if (chatOwner === userNick) {
      // 나의 채팅
      const myChat = document.createElement("div");
      const myChatHeader = document.createElement("div");
      const myChatdiv = document.createElement("div");
      const myChatText = document.createElement("div");
      const myChatNick = document.createElement("div");
      const myChatTime = document.createElement("div");
      const myChatImage = document.createElement("img");
      const myFullImage = document.createElement("img");
      const myFullText = document.createElement("div");
      const myFullTime = document.createElement("div");
      const myFullDiv = document.createElement("div");
      const myFullHeader = document.createElement("div");
      const myFullChat = document.createElement("div");
      myChatText.textContent = chatMessage;
      myChatNick.textContent = chatOwner;
      myChatTime.textContent = chatTime;
      myChatTime.classList.add("ChatTime");
      if (chatImage) {
        myChatImage.src =
          "https://dm51j1y1p1ekp.cloudfront.net/profile/" + chatImage;
        myChatImage.height = 30;
        myChatImage.width = 30;
        myChatImage.style.borderRadius = "50%";
      } else {
        myChatImage.src = withview;
        myChatImage.height = 30;
        myChatImage.width = 30;
        myChatImage.style.borderRadius = "50%";
      }
      myFullText.textContent = `: ${chatMessage}`;
      myFullTime.textContent = chatTime;
      if (chatImage) {
        myFullImage.src =
          "https://dm51j1y1p1ekp.cloudfront.net/profile/" + chatImage;
        myFullImage.height = 30;
        myFullImage.width = 30;
        myFullImage.style.borderRadius = "50%";
      } else {
        myFullImage.src = withview;
        myFullImage.height = 30;
        myFullImage.width = 30;
        myFullImage.style.borderRadius = "50%";
      }

      // 사이드바 채팅
      myChat.appendChild(myChatImage);
      myChatHeader.appendChild(myChatNick);
      myChatHeader.appendChild(myChatTime);
      myChatdiv.appendChild(myChatHeader);
      myChatdiv.appendChild(myChatText);
      myChat.appendChild(myChatdiv);
      myChat.classList.add("myChat");
      myChatdiv.classList.add("myChatdiv");
      myChatHeader.classList.add("myChatHeader");
      chatLogSide.appendChild(myChat);

      // 전체화면 채팅
      myFullHeader.appendChild(myFullImage);
      // myFullHeader.appendChild(myFullTime);
      myFullDiv.appendChild(myFullHeader);
      myFullDiv.appendChild(myFullText);
      myFullChat.appendChild(myFullDiv);
      myFullChat.classList.add("myChatF");
      myFullDiv.classList.add("myChatdivF");
      myFullHeader.classList.add("myChatHeaderF");
      chatLogFull.appendChild(myFullChat);
    } else {
      // 남의 채팅
      const yourChat = document.createElement("div");
      const yourChatHeader = document.createElement("div");
      const yourChatdiv = document.createElement("div");
      const yourChatText = document.createElement("div");
      const yourChatNick = document.createElement("div");
      const yourChatTime = document.createElement("div");
      const yourChatImage = document.createElement("img");
      const yourFullImage = document.createElement("img");
      const yourFullText = document.createElement("div");
      const yourFullTime = document.createElement("div");
      const yourFullDiv = document.createElement("div");
      const yourFullHeader = document.createElement("div");
      const yourFullChat = document.createElement("div");
      yourChatText.textContent = chatMessage;
      yourChatNick.textContent = chatOwner;
      yourChatTime.textContent = chatTime;
      yourChatTime.classList.add("ChatTime");
      if (chatImage) {
        yourChatImage.src =
          "https://dm51j1y1p1ekp.cloudfront.net/profile/" + chatImage;
        yourChatImage.height = 30;
        yourChatImage.width = 30;
        yourChatImage.style.borderRadius = "50%";
      } else {
        yourChatImage.src = withview;
        yourChatImage.height = 30;
        yourChatImage.width = 30;
        yourChatImage.style.borderRadius = "50%";
      }
      yourFullText.textContent = `: ${chatMessage}`;
      yourFullTime.textContent = chatTime;
      if (chatImage) {
        yourFullImage.src =
          "https://dm51j1y1p1ekp.cloudfront.net/profile/" + chatImage;
        yourFullImage.height = 30;
        yourFullImage.width = 30;
        yourFullImage.style.borderRadius = "50%";
      } else {
        yourFullImage.src = withview;
        yourFullImage.height = 30;
        yourFullImage.width = 30;
        yourFullImage.style.borderRadius = "50%";
      }

      // 사이드바 채팅
      yourChat.appendChild(yourChatImage);
      yourChatHeader.appendChild(yourChatNick);
      yourChatHeader.appendChild(yourChatTime);
      yourChatdiv.appendChild(yourChatHeader);
      yourChatdiv.appendChild(yourChatText);
      yourChat.appendChild(yourChatdiv);
      yourChat.classList.add("yourChat");
      yourChatdiv.classList.add("yourChatdiv");
      yourChatHeader.classList.add("yourChatHeader");
      chatLogSide.appendChild(yourChat);

      // 전체화면 채팅
      yourFullHeader.appendChild(yourFullImage);
      // yourFullHeader.appendChild(yourFullTime);
      yourFullDiv.appendChild(yourFullHeader);
      yourFullDiv.appendChild(yourFullText);
      yourFullChat.appendChild(yourFullDiv);
      yourFullChat.classList.add("yourChatF");
      yourFullDiv.classList.add("yourChatdivF");
      yourFullHeader.classList.add("yourChatHeaderF");
      chatLogFull.appendChild(yourFullChat);
    }
  }

  function goToCreateYard() {
    leaveSession();
    navigate("/board");
  }

  return (
    <>
      {/* 전체 화면 */}
      <div className="groupchat">
        {/* 캔버스 화면 */}
        <div id="video-chat-main-container">
          <div id="join">
            <div id="join-dialog">
              {/* <h1>Join a video session</h1>
              <form className="form-group" >
                <p>
                  <label>Participant</label>
                  <input className="form-control" type="text" id="userName" />
                </p>
                <p>
                  <label>Session</label>
                  <input className="form-control" type="text" id="sessionId" />
                </p>
                <p className="text-center">
                  <input type="submit" name="commit" value="Join!" />
                </p>
              </form> */}
            </div>
          </div>
          <div id="delete-img-menu">
            <div>
              <button
                className="delete-button-class"
                onClick={deleteClickListener}
                id="delete-button"
                data-imgid=""
              >
                Delete
              </button>
            </div>
          </div>

          <div id="channel-screen"></div>
          <div id="shared-screen"></div>
          <div className="cam-dev-block">
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
              <div id="remote-container" className="none"></div>
              <div id="container-screens" className="sharing-Screen"></div>
              <div id="img-container"></div>
            </div>
          </div>
        </div>
        {/* exit버튼 */}
        <div
          className={fullscreen ? "groupchat-exit" : "groupchat-exit-hidden"}
        >
          <Link to="/">
            <img src={exit} alt="" onClick={leaveSession} />
          </Link>
        </div>
        {/* 전체화면 종료 */}
        <div>
          <img
            src={back}
            alt="엑스"
            onClick={pushX}
            className={fullscreen ? "fullscreen-x-hidden" : "fullscreen-x"}
          />
        </div>
        {/* 좌측에서 나올 메뉴들 */}
        <div>
          {/* 세팅 */}
          <div
            className={settingsClicked ? "side-menu-div-on" : "side-menu-div"}
          >
            <div className="setting-menu-div">
              <div className="accordion">
                <div className="accordion-item">
                  <div
                    className="accordion-header"
                    onClick={openFriendListInNewWindow}
                    style={{ cursor: "pointer" }}
                  >
                    내 친구
                  </div>
                  <div className="accordion-content"></div>
                </div>
              </div>
              <div className="accordion">
                <div className="accordion-item">
                  <div className="accordion-header" onClick={goToCreateYard}>
                    창작 마당
                  </div>
                  <div className="accordion-content"></div>
                </div>
              </div>
              <div className="accordion">
                <div className="accordion-item">
                  <div
                    id="buttonScreenShare"
                    className="accordion-header"
                    onMouseUp={publishScreenShare}
                  >
                    화면 공유
                  </div>
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
                <div className="accordion-item" onMouseUp={deleteChannel}>
                  <div className="accordion-header">채널 지우기</div>
                  <div className="accordion-content"></div>
                </div>
              </div>
              <div className="accordion">
                <div
                  className="accordion-item"
                  onMouseUp={presetRegistSettings}
                >
                  <div className="accordion-header">프리셋 등록</div>
                  <div className="accordion-content"></div>
                </div>
                <div className="scroll-container">
                  <PresetRegistModal
                    isOpen={presetRegisterClicked}
                    onChange={presetRegistSettings}
                    stage={stage.current}
                  ></PresetRegistModal>
                </div>
              </div>
              <div className="accordion">
                <div
                  className="accordion-item"
                  onMouseUp={presetLoadClickedChange}
                >
                  <div className="accordion-header">프리셋 불러오기</div>
                  <div className="accordion-content"></div>
                </div>
                <div className="scroll-container">
                  <PresetLoadModal
                    isOpen={presetLoadClicked}
                    onChange={presetLoadClickedChange}
                    loadCanvas={sendStageInfo}
                  ></PresetLoadModal>
                </div>
              </div>
            </div>
          </div>
          {/* 스티커와 배경화면 */}
          <div
            className={stickerClicked ? "side-menu-div-on" : "side-menu-div"}
          >
            <div id="sticker-and-backimg">
              <span
                className={stickerAndBg ? "stk-bg-selecter" : ""}
                onClick={stickermenuSettings}
              >
                스티커
              </span>
              <p>|</p>
              <span
                className={stickerAndBg ? "" : "stk-bg-selecter"}
                onClick={stickermenuSettings}
              >
                배경화면
              </span>
            </div>
            <div
              className={
                stickermenuClicked ? "sticker-menu-on" : "sticker-menu"
              }
            >
              <StickerContainer
                title="스티커"
                table="stickers"
                addFile={stickertemp}
                imgDirectory="sticker"
              ></StickerContainer>
            </div>

            <div
              className={
                stickermenuClicked ? "sticker-menu" : "sticker-menu-on"
              }
            >
              <StickerContainer
                title="배경화면"
                table="backgrounds"
                addFile={addBackImageClick}
                imgDirectory="channel-background"
              ></StickerContainer>
            </div>
          </div>
          {/* 채팅 */}
          <div className={chatClicked ? "side-menu-div-on" : "side-menu-div"}>
            <div className="chat-menu-div">
              <div id="chatLogSide" className="groupchat-log-div"></div>
              <div className="chat-input-div">
                <input
                  type="text"
                  value={inputText}
                  onChange={fullscreenInputChange}
                  className="chat-input-kan"
                  onKeyUp={handleTempButtonClick}
                  placeholder="메세지 보내기 !"
                />
              </div>
            </div>
          </div>
        </div>
        {/* 하단 컨트롤 전체 */}
        <div
          className={
            fullscreen ? "underbar-container" : "underbar-container-hidden"
          }
        >
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
                  {userProfile ? (
                    <img src={profileUrl} alt="" className="user" />
                  ) : (
                    <img src={withview} alt="" className="user" />
                  )}
                  <div className="user-stat">
                    <span className="username">{userNick}</span>
                  </div>
                </div>
              </button>

              {/* 마이크 조절 버튼 */}
              <button
                className="underbar button is-rounded"
                id="mic"
                onClick={micSettings}
              >
                <img src={micClicked ? micoff : micon} alt="" />
              </button>
              {/* 볼륨조절 */}
              <button
                className="underbar button is-rounded"
                id="vol"
                onClick={volSettings}
              >
                <img src={volClicked ? voloff : volon} alt="" />
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
              className={
                msgClicked
                  ? "underbar button is-rounded new-message"
                  : "underbar button is-rounded a-new-message"
              }
              onClick={() => setmsgClicked(true)}
            >
              <img
                src={
                  lastChat == null ||
                  lastChat.userDto.profileImgSearchName == null
                    ? withview
                    : `https://dm51j1y1p1ekp.cloudfront.net/profile/${lastChat.userDto.profileImgSearchName}`
                }
                alt=""
                className="user"
              />
              <div className="last-message">
                {lastChat == null ? "" : lastChat.message}
              </div>
            </button>

            {/* 스크린 샷 */}
            <button
              className="underbar"
              onClick={"function-here"}
              id={"screenshot"}
            >
              <img src={camera} alt="" />
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
              onClick={() => {
                chatSettings();
              }}
              id={chatClicked ? "achat" : "chat"}
            >
              <img src={chat} alt="" />
            </button>
          </div>
        </div>
        {/* 채팅창 */}
        <div
          className={fullscreen ? "fullscreen-chat-hidden" : "fullscreen-chat"}
        >
          <div
            id="chat-container"
            className={
              fullsceenChatLog
                ? "fullscreen-chatlog"
                : "fullscreen-chatlog-hidden"
            }
          >
            <div id="chatLogFull" className="groupchat-log-div"></div>
          </div>
        </div>
        {/* 임시 인풋 */}
        <div
          className={
            fullscreen
              ? "fullscreen-chat-input-kan-div-hidden"
              : "fullscreen-chat-input-kan-div"
          }
        >
          <input
            type="text"
            value={inputText}
            onChange={fullscreenInputChange}
            className="fullscreen-chat-input-kan"
            onKeyUp={handleTempButtonClick}
            placeholder="메세지 보내기 !"
          />
        </div>
        {/* 얼굴 */}
        <div
          className={fullscreen ? "fullscreen-face-hidden" : "fullscreen-face"}
        >
          <div id="speakingdiv" className="speakingdiv"></div>
          <div id="speakingName" className="speakingName"></div>
        </div>
        <div ref={divRef} id="bigfulldiv" className="bigfulldiv"></div>
      </div>
    </>
  );
}
