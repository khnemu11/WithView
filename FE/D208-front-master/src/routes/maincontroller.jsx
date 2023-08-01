import React, { useState, useEffect } from "react";
import Mainpage from "./components/mainpage";
import FirstMain from "./components/firstmain";

const MainController = () => {
  const [hasJoinedServer, setHasJoinedServer] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Replace this with your actual API call
    fetch("API URL FOR CHECKING JOINED SERVERS")
      .then((response) => response.json())
      .then((data) => {
        if (data.joinedServers.length > 0) {
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
  }, []);

  if (isLoading) {
    return <div>Loading...</div>; // Loading spinner or skeleton can be used here
  }

  return hasJoinedServer ? <Mainpage /> : <FirstMain />;
};

export default MainController;
