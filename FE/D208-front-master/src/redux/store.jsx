// src/redux/store.js

import { createStore,compose } from "redux";
import { persistStore, persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage"; // defaults to localStorage for web
import rootReducer from "./reducers";


// Redux Persist 설정
const persistConfig = {
    key: "root", // 저장될 키 이름
    storage, // 어떤 스토리지에 저장할지 (여기서는 localStorage)
  };

const persistedReducer = persistReducer(persistConfig, rootReducer);
const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;
const store = createStore(persistedReducer, composeEnhancers());
const persistor = persistStore(store); // Redux Persist를 초기화


export { store, persistor };
