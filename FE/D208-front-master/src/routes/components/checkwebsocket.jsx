import React from "react";
import { useEffect } from "react";
import { useSelector } from "react-redux";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { useDispatch } from "react-redux";
import { setStomp } from "../../redux/actions/stompActions";

export default function Checkwebsocket() {
  const dispatch = useDispatch();
  const stomp = useSelector((state) => state.stomp);
  const userPk = useSelector((state) => state.user.seq);
  const url = "https://i9d208.p.ssafy.io/api";

  useEffect(() => {
    if (stomp === null) {
      const sock = new SockJS(`${url}/ws-stomp`); // 연결되는 웹소켓의 end-point
      const stomp = Stomp.over(sock); // Stomp 연결
      stomp.connect(
        {
          userSeq: userPk,
        },
        (res) => {
          console.log(res);
          // dispatch(setStomp(stomp))
        },
        (err) => {
          console.log(err);
        }
      );
      dispatch(setStomp(stomp));
    }
  }, [stomp]);
}
