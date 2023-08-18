export const setStomp = (stomp) => ({
    type: "SET_STOMP",
    payload: stomp,
  });
  
export const clearStomp = () => ({
    type: "CLEAR_STOMP",
  });