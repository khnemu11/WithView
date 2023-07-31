// src/redux/reducers/tokenReducer.js


const tokenReducer = (state = null, action) => {
    switch (action.type) {
      case "SET_TOKEN":
        return action.payload
      case "CLEAR_TOKEN":
        return null;
      default:
        return state;
    }
  };
  
  export default tokenReducer;
  