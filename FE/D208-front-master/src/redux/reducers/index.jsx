// src/redux/reducers/index.js

import { combineReducers } from "redux";
import tokenReducer from "./tokenReducer";
import userReducer from "./userReducer";

// 여러 개의 리듀서를 합치는 경우 여기에 추가합니다.
const rootReducer = combineReducers({
  // 토큰을 저장할 리듀서 추가
  token: tokenReducer,
  user : userReducer,
});

export default rootReducer;
