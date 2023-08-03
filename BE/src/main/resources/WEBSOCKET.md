## 웹소켓의 흐름도

1. 회원이


1. 회원이 서버에 입장한다.
    1. 프론트엔드에서 입장 버튼과 함께 웹소켓 연결을 시도
        ``` javascript
        // 예시코드
        var sock = new SockJS("/api/ws-stomp"); // 연결되는 웹소켓의 end-point
        var ws = Stomp.over(sock); // Stomp 연결
     
        ws.connect({"id" : {id}, "userseq" : {userseq}, "serverseq" : {serverseq}},
             function(frame){}); // 웹소켓 진짜 연결
        ```  

    2. 백엔드에서는 handler에서 웹소켓이 CONNECT될 때, 레디스에 유저 아이디와 서버에 대한 정보를 저장함
       그리고 리턴값으로 현재 서버의 채널에 참여중인 사람들의 목록을 채널별로 정리해서 뿌려줌

2. 회원이 채널에 입장한다.
    1. 프론트엔드에서 입장 버튼과 함께 웹소켓 구독을 시도
   ```javascript
      ws.subscribe("/api/sub/chat/{serverseq}/{channelseq}", function(message) {
         var recieve = JSON.parse(message.body); // message에 채팅내용이 담겨서 옴
         recvMessage(recieve); // 채팅내용을 html로 바꿔주는 함수 실행
   })
   // --> subscribe 함수로 해당 url의 웹소켓 연결을 구독하는 상태가 되며, 백엔드에서 해당 url로
   // publish를 해주면 바로 응답을 받아 html로 처리함.
   ```
    2. 백엔드에서는 handler에서 웹소켓이 SUBSCRIBE될 때, 레디스에 유저아이디, 채널정보를 저장함.
       또한 publish함수가 실행될 경우 메시지 보내주는 기능 있음

3. 회원이 다른 채널을 클릭해 