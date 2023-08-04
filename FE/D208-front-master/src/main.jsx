import * as React from "react";
import * as ReactDOM from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { Provider } from "react-redux";
// import store from "./store";
import { PersistGate } from "redux-persist/integration/react"; // Redux Persist를 사용하기 위한 컴포넌트
import { store, persistor } from "./redux/store";
import Root from "./routes/root";
import Hello from "./routes/hello";
import Login from "./routes/login";
import Signup from "./routes/signup";
import "./css/main.css"; // Import the CSS file
import GroupChat from "./routes/groupchat";
import GroupChattemp from "./routes/groupchattemp";
import FullScreen from "./routes/fullscreen";
import Profile from "./routes/profile";
import ServerPlus from "./routes/serverplus";
import Serverpage from "./routes/serverpage";
import MainController from "./routes/maincontroller";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Root />,
  },
  {
    path: "contacts/:contactId",
    element: <Hello />,
  },
  {
    path: "/groupchat",
    element: <GroupChat />,
  },
  {
    path: "/groupchattemp",
    element: <GroupChattemp />,
  },
  {
    path: "/login",
    element: <Login />,
  },
  {
    path: "/signup",
    element: <Signup />,
  },
  {
    path: "/mainpage",
    element: <MainController />,
  },
  {
    path: "/fullscreen",
    element: <FullScreen />,
  },
  {
    path: "/profile",
    element: <Profile />,
  },
  {
    path: "/serverplus",
    element: <ServerPlus />,
  },
  {
    path: "/server",
    element: <Serverpage />,
  },
]);

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    {/* <Header /> */}
    <Provider store={store}>
      <PersistGate loading={null} persistor={persistor}>
        <RouterProvider router={router} />
      </PersistGate>
    </Provider>
  </React.StrictMode>
);
