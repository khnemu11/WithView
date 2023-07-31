//konva 오브젝트 아이디 룰
//이미지 : img-로 시작
//영상 : 이용자의 아이디 

var OV;	//오픈비두 변수
var session;	//현재 채널 이름(오픈비두에선 채팅방 단위를 'session'이라고 부름)
let videoContainer = document.querySelector("#video-container"); //오픈비두로 받은 영상을 담은 컨테이너
let port = 9091; //백엔드 포트 번호
let domain = 'localhost';	//도메인 주소
let APPLICATION_SERVER_URL = `http://${domain}:${port}/`;
let userId; //유저 아이디
let remoteVideoLayer = new Konva.Layer(); //소켓에 저장된 비디오 레이어(최초 접속시 한번 사용)
let remoteImageLayer = new Konva.Layer(); //소켓에 저장된 이미지 레이어(최초 접속시 한번 사용)

//메인 캔버스(스테이지)
let stage = new Konva.Stage({
	container: 'channel-screen',
	width: document.body.offsetWidth,
	height: document.body.offsetWidth 
});

stage.add(new Konva.Layer()); //비디오 레이어(인덱스 0번)
stage.add(new Konva.Layer()); //이미지 레이어(인덱스 1번)

//요청 들어온 캔버스(보여주지 않으므로 크기를 0으로)
let remoteStage = new Konva.Stage({
	container: 'shared-screen',
	width: 0,
	height: 0
})
remoteStage.hide();

let socket; //소켓 통신 변수
//소켓 메시지가 오면 반응하는 메소드
//소켓을 통신하는 것들 (채팅, 캔버스 json파일)
async function onMessage(message) {
	console.log("message from socket!");
	//받은 소켓 메시지를 JSON파일로 파싱
	let data = JSON.parse(message.data);

	//캔버스 내용이 변경되면 실행
	if(data.type == 'change'){
		console.log("캔버스 변화");
		loadCanvasChange(data.object);
	}
	//처음 채널에 참가하면 실행
	else if(data.type=='join'){
		//비디오 레이어 로드
		if(data.video!=null){
			console.log("서버에 있는 비디오 레이어");
			remoteVideoLayer = Konva.Node.create(data.video);
			console.log(remoteVideoLayer);
		}
		//이미지 레이어 로드
		if(data.image!=null){
			console.log("서버에 있는 이미지 레이어");
			remoteImageLayer = Konva.Node.create(data.image);
			let images = remoteImageLayer.find('Image');

			for(let i=0;i<images.length;i++){
				var tr = new Konva.Transformer();
				tr.nodes([images[i]]);

				//이미지 변수 생성
				var imageObj = new Image();
				var imageName = images[i].getAttr('id').substring(4); //img- 제거한 나머지를 이름으로 설정
				imageObj.src = './resources/images/'+imageName;
				
				//로딩돠면 
				imageObj.onload = function(){
					images[i].setAttr('image',imageObj);
				}

				//드래그 반응이 끝나면 캔버스 넘기기
				images[i].on('dragend',function(){
					changeCanvas(images[i]);
				})
				
				//비디오의 모양을 변경하면 캔버스 변경사항을 다른사람에게 전송
				images[i].on('transformend',function(){
					changeCanvas(images[i]);
				})

				stage.children[1].add(tr);
				stage.children[1].add(images[i]);
			}
		}
	}
}
//켄버스에 변경 내용을 저장하는 함수
function loadCanvasChange(data){
	let object = Konva.Node.create(data); //변경된 오브젝트
	let objectId = object.getAttr("id");
	console.log("소캣에서 넘어온 객체 아이디");
	console.log(objectId);

	let target = stage.find("#"+objectId);	//객체 탐색

	console.log("현재 대상 객체");
	console.log(target);

	//기존 스테이지에 없는경우(이미지가 추가된 경우)
	if(target.length == 0){
		//이미지 삽입
		if(objectId.indexOf("img-")!=-1){
			var tr = new Konva.Transformer();
			tr.nodes([object]);

			//이미지 변수 생성
			var imageObj = new Image();
			var imageName = objectId.substring(4); //img- 제거한 나머지를 이름으로 설정
			imageObj.src = './resources/images/'+imageName;
			
			//로딩돠면 
			imageObj.onload = function(){
				object.setAttr('image',imageObj);
			}

			//드래그 반응이 끝나면 캔버스 넘기기
			object.on('dragend',function(){
				changeCanvas(object);
			})
			
			//비디오의 모양을 변경하면 캔버스 변경사항을 다른사람에게 전송
			object.on('transformend',function(){
				changeCanvas(object);
			})

			stage.children[1].add(tr);
			stage.children[1].add(object);``
		}
	}else{
		//이미지 위치 변경
		if(objectId.indexOf("img-")!=-1){
			target[0].setAttrs(object.getAttrs());
		}
		//영상 위치 변경
		else{
			target[0].setAttrs(object.getAttrs());
		}
	}	
}
// 소켓에서 나갔을 때
function onClose(evt) {
	console.log("소켓 종료");
}
// 소켓으로 들어왔을 때
function onOpen(evt) {
	console.log("소켓 접속");
}
//오픈비두 기본함수
//채널에 들어오면 sessionId와 userName(유저 아이디)를 통해 채널에 들어가는 로직이 들어가 있는 함수
//제일 중요함
/* OPENVIDU METHODS */
function joinSession() {
	var mySessionId = document.getElementById("sessionId").value;
	var myUserName = document.getElementById("userName").value;

	//참가한 채널 명을 url로 구분하도록 커스터마이징함
	socket = new SockJS(`http://${domain}:${port}/socket?channelId=${mySessionId}`,null, {transports: ["websocket", "xhr-streaming", "xhr-polling"]});
	socket.onmessage = onMessage;
	socket.onclose = onClose;
	socket.onopen = onOpen;

	// --- 1) Get an OpenVidu object ---
	OV = new OpenVidu();

	// --- 2) Init a session ---
	session = OV.initSession();

	// --- 3) Specify the actions when events take place in the session ---
	// On every new Stream received...
	session.on('streamCreated', event => {
		// Subscribe to the Stream to receive it. HTML video will be appended to element with 'video-container' id
		var subscriber = session.subscribe(event.stream, 'video-container');

		// When the HTML video has been appended to DOM...
		subscriber.on('videoElementCreated', event => {
			console.log("접속자 비디오 생성");
			// Add a new <p> element for the user's nickname just below its video
			appendUserData(event.element, subscriber.stream.connection);
			addVideoInCanvas(event.element,subscriber.stream.connection);
		});
	});

	// On every Stream destroyed...
	session.on('streamDestroyed', event => {
		console.log("접속자 비디오 나감");
		// Delete the HTML element with the user's nickname. HTML videos are automatically removed from DOM
		removeUserInCanvas(event.stream.connection.connectionId);
		removeUserData(event.stream.connection);
	});

	// On every asynchronous exception...
	session.on('exception', (exception) => {
		console.warn(exception);
	});
	// --- 4) Connect to the session with a valid user token ---

	// Get a token from the OpenVidu deployment
	getToken(mySessionId).then(token => {
		// First param is the token got from the OpenVidu deployment. Second param can be retrieved by every user on event
		// 'streamCreated' (property Stream.connection.data), and will be appended to DOM as the user's nickname
		session.connect(token, { clientData: myUserName })
			.then(() => {

				// --- 5) Set page layout for active call ---
				document.getElementById('session-title').innerText = mySessionId;
				document.getElementById('join').style.display = 'none';
				document.getElementById('session').style.display = 'block';

				// --- 6) Get your own camera stream with the desired properties ---
				var publisher = OV.initPublisher('video-container', {
					audioSource: undefined, // The source of audio. If undefined default microphone
					videoSource: undefined, // The source of video. If undefined default webcam
					publishAudio: true,  	// Whether you want to start publishing with your audio unmuted or not
					publishVideo: true,  	// Whether you want to start publishing with your video enabled or not
					resolution: '640x480',  // The resolution of your video
					frameRate: 30,			// The frame rate of your video
					insertMode: 'APPEND',	// How the video is inserted in the target element 'video-container'
					mirror: false       	// Whether to mirror your local video or not
				});

				// --- 7) Specify the actions when events take place in our publisher ---
				// When our HTML video has been added to DOM...
				publisher.on('videoElementCreated', function (event) {
					console.log("내 비디오 시작");
					initMainVideo(event.element, myUserName);
					appendUserData(event.element, myUserName);
					addVideoInCanvas(event.element,myUserName);
					event.element['muted'] = true;
				});

				// --- 8) Publish your stream ---
				session.publish(publisher);

				let userData = JSON.parse(session.connection.data);
				userId = userData.clientData;
			})
			.catch(error => {
				console.log('There was an error connecting to the session:', error.code, error.message);
			});
	});
}
//오픈비두 예제함수
//세션 나가기를 하면 채널을 나가는 로직이 들어가 있는 함수
//꼭 필요함
function leaveSession() {
	// --- 9) Leave the session by calling 'disconnect' method over the Session object ---
	session.disconnect();

	// Removing all HTML elements with user's nicknames.
	// HTML videos are automatically removed when leaving a Session
	removeAllUserData();

	//그런데 이 밑은 화상 채팅과 채널명 입력을 바꾸는 곳이라 필요 없음
	// Back to 'Join session' page
	document.getElementById('join').style.display = 'block';
	document.getElementById('session').style.display = 'none';
}
//오픈비두 예제함수
//창이 꺼지면 소켓을 나가는 함수
//꼭 필요함
window.onbeforeunload = function () {
	if (session) session.disconnect();
};
/* APPLICATION SPECIFIC METHODS */
//오픈비두 예제 함수
//페이지가 로드가 되면 기본값으로 채널 명과 랜덤한 유저 아이디를 생성해줌
//사용 안할듯
window.addEventListener('load', function () {
	generateParticipantInfo();
});
//오픈비두 예제 함수
//채널 명과 랜덤한 유저 아이디를 생성해줌
//사용 안할듯
function generateParticipantInfo() {
	document.getElementById("sessionId").value = "SessionA";
	document.getElementById("userName").value = "Participant" + Math.floor(Math.random() * 100);
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
	var dataNode = document.createElement('div');
	dataNode.className = "data-node";
	dataNode.id = "data-" + nodeId;
	dataNode.innerHTML = "<p>" + userData + "</p>";
	videoElement.parentNode.insertBefore(dataNode, videoElement.nextSibling);
	addClickListener(videoElement, userData);
}
//캔버스에 영상을 삭제하는 함수
function removeUserInCanvas(connectionId){
	var dataNode = document.getElementById("data-" +connectionId);
	var targetId = dataNode.innerText;
	//트랜스포머랑 비디오 삭제
	for(var i=0;i<stage.children[0].children.length;i++){
		console.log(stage.children[0].children[i].getAttr('id') +" vs "+targetId);
		if(stage.children[0].children[i].getAttr('id') == targetId){
			console.log(stage.children[0]);
			stage.children[0].children[i-1].remove();
			console.log(stage.children[0]);
			stage.children[0].children[i-1].remove();
			break;
		}
	}
	updateCanvasOnlyServer();
}
//이용자 나감/튕김으로 인한 캔버스 내용 변경을 서버에 저장하는 함수
function updateCanvasOnlyServer(){
	let jsonData = {};
	
	//stage 저장 및 바뀐 객체 전송
	jsonData['video'] = stage.children[0];
	jsonData['image'] = stage.children[1];
	jsonData['type'] = 'update';
	jsonData['channel'] = session.sessionId;

	socket.send(JSON.stringify(jsonData));
}
//캔버스에 영상을 넣는 함수
function addVideoInCanvas(videoElement,connection){
	var connectionId = connection;

	//자기 자신의 영상의 커넥션 아이디 : 로그인한 유저 닉네임
	//다른사람의 커넥션 아이디 : 커넥션 고유 번호
	//자기 자신인 경우
	//비디오 영상 레이어 꺼내기
	let layer = stage.children[0];
	
	//비디오 영상 초기 디자인
	var video;

	//자기 자신영상인 경우
	if(typeof connection === 'string'){
		connectionId = connection;
	}
	//다른 사람인 경우
	else{
		//다른사람의 로그인 유저 닉네임은 비디오 컨테이너의 data- 중 p태그에 있다
		connectionId = document.querySelector('#data-'+connection.connectionId+' p').textContent;
	}
	//이미 채널에 참가한 사람의 영상인지 아닌지 비디오 레이어에서 찾는 코드
	let remoteVideo = remoteVideoLayer.find("#" + connectionId);

	var video;
	
	//자기 자신인 경우
	if(remoteVideo.length==0){
		video = new Konva.Image({
			x: 10,
			y: 10,
			width: 300,
			height: 300,
			image:videoElement, 
			draggable: true,
			id:connectionId, //수정하면 안됨!!
			cornerRadius: 150,
		});
	}

	//채녈에 참가한 사람의 영상이 아닌 경우(참가자)
	else{
		video = new Konva.Image({
			x: remoteVideo[0].getAttr('x'),
			y: remoteVideo[0].getAttr('y'),
			width: remoteVideo[0].getAttr('width'),
			height: remoteVideo[0].getAttr('height'),
			image:videoElement, 
			draggable: true,
			id:connectionId, //수정하면 안됨!!
			cornerRadius: 150,
		});
	}

	//비디오 크기 및 회전을 도와주는 객체
	var tr = new Konva.Transformer();
	tr.nodes([video]);
	
	//비디오 실행
	var animation = new Konva.Animation(function () {
	}, layer);

	animation.start();

	layer.add(tr);
	layer.add(video);
	
	//자기 자신의 비디오 경우 자동으로 실행이 되지 않는 오류가 있어 직접 실행
	videoElement.oncanplaythrough = function(){
		videoElement.play();
	}

	//비디오를 움직이면 캔버스 변경사항을 다른사람에게 전송
	video.on('dragend',function(){
		changeCanvas(video);
	})
	
	//비디오의 모양을 변경하면 캔버스 변경사항을 다른사람에게 전송
	video.on('transformend',function(){
		changeCanvas(video);
	})
}
//캔버스의 내용 변경을 감지했을 때 캔버스 데이터를 JSON으로 소켓통신하는 함수
function changeCanvas(element){
	console.log("element 변경됨!");
	console.log(element.toJSON());
	let jsonData = {};
	
	//stage 저장 및 바뀐 객체 전송
	jsonData['video'] = stage.children[0];	//비디오 레이어
	jsonData['image'] = stage.children[1];	//이미지 레이어
	jsonData['type'] = 'change';	//소캣 메시지 타입
	jsonData['object'] = element.toJSON(); //변경된 오브젝트
	jsonData['channel'] = session.sessionId;	//채널명

	console.log(JSON.stringify(jsonData));
	socket.send(JSON.stringify(jsonData));	//소켓 전송
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
	var nicknameElements = document.getElementsByClassName('data-node');
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
	videoElement.addEventListener('click', function () {
		var mainVideo = $('#main-video video').get(0);
		if (mainVideo.srcObject !== videoElement.srcObject) {
			$('#main-video').fadeOut("fast", () => {
				$('#main-video p').html(userData);
				mainVideo.srcObject = videoElement.srcObject;
				$('#main-video').fadeIn("fast");
			});
		}
	});
}
//오픈비두 기본 함수
//메인화면 초기화 하는 함수
//사용 안함
function initMainVideo(videoElement, userData) {
	document.querySelector('#main-video video').srcObject = videoElement.srcObject;
	document.querySelector('#main-video p').innerHTML = userData;
	document.querySelector('#main-video video')['muted'] = true;
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
	return createSession(mySessionId).then(sessionId => createToken(sessionId));
}

//오픈비두 기본 함수
//최초 접속 시 채팅방을 생성하는 함수
function createSession(sessionId) {
	return new Promise((resolve, reject) => {
		$.ajax({
			type: "POST",
			url: APPLICATION_SERVER_URL + "api/sessions",
			data: JSON.stringify({ customSessionId: sessionId }),
			headers: { "Content-Type": "application/json" },
			success: response => resolve(response), // The sessionId
			error: (error) => reject(error)
		});
	});
}
//채팅방의 정보를 가져오는 함수
function createToken(sessionId) {
	return new Promise((resolve, reject) => {
		$.ajax({
			type: 'POST',
			url: APPLICATION_SERVER_URL + 'api/sessions/' + sessionId + '/connections',
			data: JSON.stringify({}),
			headers: { "Content-Type": "application/json" },
			success: (response) => resolve(response), // The token
			error: (error) => reject(error)
		});
	});
}
//버튼을 누르면 이미지가 생성되는 함수
document.querySelector("#button").addEventListener('click',function(){
	console.log("click");
	console.log(papago);
	var imageObj = new Image();
	imageObj.src = './resources/images/papago.png';
	var papago; //이미지 객체

	//이미지는 바로 로딩이 되지 않기 때문에 이미지가 로딩되면 객체를 생성하는 함수
	imageObj.onload = function(){
		layer = stage.children[1];		

		//이미지 생성
		papago = new Konva.Image({
			x: 50,
			y: 50,
			image : imageObj,
			width: 106,
			height: 118,
			id : 'img-papago.png',
			draggable:true
		});

		var tr = new Konva.Transformer();
		
		tr.nodes([papago]);

		//이미지를 움직이면 캔버스 변경사항을 다른사람에게 전송
		papago.on('dragend',function(){
			changeCanvas(papago);
		})
		
		//이미지의 모양을 변경하면 캔버스 변경사항을 다른사람에게 전송
		papago.on('transformend',function(){
			changeCanvas(papago);
		})

		layer.add(tr);
		layer.add(papago);

		//이미지 생성을 다른 사람들한테도 넘기는 부분
		changeCanvas(papago);
	}
})