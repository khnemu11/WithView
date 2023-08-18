const stompReducer = (state = null, action) => {
    switch (action.type) {
      case "SET_STOMP":
        return action.payload
      case "CLEAR_STOMP":
        return null;
      default:
        return state;
    }
  };
  
  export default stompReducer;