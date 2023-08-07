<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.4.0/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
</head>
<h3> 채팅 페이지</h3>
<button type="button" id="conn-btn">연결</button>
<button type="button" id="sub-btn">구독</button>
<button type="button" id="enter-btn1">입장1</button>
<button type="button" id="enter-btn2">입장2</button>
<button type="button" id="leave-btn1">퇴장1</button>
<button type="button" id="leave-btn2">퇴장2</button>
<button type="button" id="chat-sub-btn">채팅 구독</button>
<input id="chat">
<button type="button" id="chat-btn">채팅보내기</button>
</body>
<script>
    let socket;
    let stomp;
    let serverSeq = 9;
    let findUserInChannelsUrl = `/api/sub/server/9`; //서버내 모든 채널의 인원정보 찾기
    let severEnterSendUrl = `/api/pub/server/9/enter`; //serverSeq의 서버 입장

    function onConnected() {
        console.log("소켓 연결 성공");
    }

    function onError() {
        console.log("소켓 연결 실패");
    }

    document.querySelector("#conn-btn").addEventListener("click", function () {
        console.log("연결 버튼 클릭");
        socket = new SockJS('/api/ws-stomp');
        stomp = Stomp.over(socket);
        stomp.connect({"userSeq": 23}, onConnected, onError);
    });

    document.querySelector("#sub-btn").addEventListener("click", function () {
        console.log("구독 버튼 클릭");
        stomp.subscribe(findUserInChannelsUrl, function (msg) {
            console.log("넘어온 메시지");
            console.log(msg);
            let data = JSON.parse(msg.body);
            console.log("데이터");
            console.log(data);
        });
        stomp.send(severEnterSendUrl);
    });

    document.querySelector("#enter-btn1").addEventListener("click", function () {
        console.log("입장1 버튼 클릭");
        stomp.send(`/api/pub/channel/9/16/enter`, {}, JSON.stringify({"userSeq": 23}));
    })

    document.querySelector("#enter-btn2").addEventListener("click", function () {
        console.log("입장2 버튼 클릭");
        stomp.send(`/api/pub/channel/9/16/enter`, {}, JSON.stringify({"userSeq": 24}));
    })

    document.querySelector("#leave-btn1").addEventListener("click", function () {
        console.log("퇴장1 버튼 클릭");
        stomp.send(`/api/pub/channel/9/16/leave`, {}, JSON.stringify({"userSeq": 23}));
    })

    document.querySelector("#leave-btn2").addEventListener("click", function () {
        console.log("퇴장2 버튼 클릭");
        stomp.send(`/api/pub/channel/9/16/leave`, {}, JSON.stringify({"userSeq": 24}));
    })

    document.querySelector("#chat-sub-btn").addEventListener("click", function () {
        console.log("채팅 구독 버튼 클릭");
        stomp.subscribe(`/api/sub/chat/channel/16`, function (msg) {
            console.log("넘어온 메시지");
            console.log(msg);
            let data = JSON.parse(msg.body);
            console.log("데이터");
            console.log(data);
        });
    })

    document.querySelector("#chat-btn").addEventListener("click", function () {
        console.log("채팅 전송");
        let element = document.querySelector("#chat").value;
        console.log("element :" + element);
        stomp.send(`/api/pub/chat/channel/message`, {}, JSON.stringify({
            "userSeq": 23,
            "channelSeq": 16,
            "message": element
        }));
    })
    //	private String message;
    // private Long channelSeq;
    // private Long userSeq;
    // private LocalDateTime sendTime;
</script>
</body>
</html>