import React, { useState, useEffect } from "react";
import Mainpage from "./components/mainpage";
import FirstMain from "./components/firstmain";
import { useSelector } from "react-redux";
import axiosInstance from "./axiosinstance";



const MainController = () => {
  const [hasJoinedServer, setHasJoinedServer] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const userSeq = useSelector((state) => state.user.seq)
  const token = useSelector((state) => state.token);



  useEffect(() => {
    // Replace this with your actual API call
    axiosInstance.get(`/servers/find-server-by-user?userSeq=${userSeq}`, {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    })
    .then((response) => {
      const joinedServers = response.data.servers;
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
  return hasJoinedServer ? <Mainpage /> : <FirstMain />;
};

export default MainController;
