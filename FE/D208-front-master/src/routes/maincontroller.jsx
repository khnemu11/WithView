import React, { useState, useEffect } from "react";
import Mainpage from "./components/mainpage";
import FirstMain from "./components/firstmain";
import { useSelector } from "react-redux";


const MainController = () => {
  const [hasJoinedServer, setHasJoinedServer] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const userSeq = useSelector((state) => state.user.seq)


  useEffect(() => {
    // Replace this with your actual API call
    fetch(`https://i9d208.p.ssafy.io/api/servers/find-server-by-user?userSeq=${userSeq}`)
      .then((response) => response.json())
      .then((data) => {
        const joinedServers = data.servers;
        if (joinedServers.length > 0) {
          setHasJoinedServer(true);
        } else {
          setHasJoinedServer(false);
        }
        setIsLoading(false);
      })
      .catch((error) => {
        console.error("Error:", error);
        setIsLoading(false);
      });
  }, [userSeq]);

  if (isLoading) {
    return <div>Loading...</div>; //  로딩 스피너 또는 스켈레톤을 사용할 수 있습니다
  }

  return hasJoinedServer ? <Mainpage /> : <FirstMain />;
};

export default MainController;
