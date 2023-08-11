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
<button type="button" id="sub-btn">캔버스 채널 구독</button>
<button type="button" id="change-btn">캔버스 변경!</button>
</body>
<script>
    let socket;
    let stomp;
    let channelSeq = 9;
    let userSeq = 1;
    let hostUrl = "http://localhost:9091"
    let findCanvasInChannelUrl = 'http://localhost:9091/api/sub/canvas/channel/'+ channelSeq; //서버내 모든 채널의 인원정보 찾기
    let channelSendUrl = 'http://localhost:9091/api/pub/canvas/channel/'+channelSeq; //serverSeq의 서버 입장

    function onConnected() {
        console.log("소켓 연결 성공");
    }

    function onError() {
        console.log("소켓 연결 실패");
    }

    document.querySelector("#conn-btn").addEventListener("click", function () {
        console.log("연결 버튼 클릭");
        socket = new SockJS('http://localhost:9091/api/ws-stomp');
        stomp = Stomp.over(socket);
        stomp.connect({"userSeq": userSeq}, onConnected, onError);
    });

    document.querySelector("#change-btn").addEventListener("click",function(){
        let data = {};
        data.userSeq = userSeq;
        data.canvas = "변경된 캔버스 입니다.";
        data.channelSeq = channelSeq;
        data.type = "join";
        console.log("channel seq : "+channelSeq)
        console.log("url : "+`/api/pub/canvas/channel/`+channelSeq);

        stomp.send(`/api/pub/canvas/channel/`+channelSeq, {}, JSON.stringify({
            "userSeq": userSeq,
            "canvas": "변경된 캔버스 입니다.",
            "channelSeq": channelSeq,
            "type": "join"
        }));
    });

    document.querySelector("#sub-btn").addEventListener("click", function () {
        console.log("캔버스 구독 버튼 클릭");

        stomp.subscribe(findCanvasInChannelUrl, function (msg) {
            console.log("캔버스 메세지 도착!");
            console.log(msg);
            let data = JSON.parse(msg.body);
            console.log("데이터");
            console.log(data);
        });
    });
</script>
</body>
</html>