import * as React from "react";
import * as ReactDOM from "react-dom/client";
import { createBrowserRouter, RouterProvider, Outlet } from "react-router-dom";
import { Provider } from "react-redux";
import { PersistGate } from "redux-persist/integration/react"; // Redux Persist를 사용하기 위한 컴포넌트
import { store, persistor } from "./redux/store";
import Hello from "./routes/hello";
import Login from "./routes/login";
import Signup from "./routes/signup";
import "./css/main.css"; // Import the CSS file
import GroupChat from "./routes/groupchat";
// import GroupChattemp from "./routes/groupchattemp";
import FullScreen from "./routes/fullscreen";
import Profile from "./routes/profile";
import ServerPlus from "./routes/serverplus";
import Serverpage from "./routes/serverpage";
import MainController from "./routes/maincontroller";
import Mainpage from "./routes/components/mainpage";
import ServerEnter from "./routes/serverenter";
import FindId from "./routes/findid";
import FindPassword from "./routes/findpassword";
import ServerEdit from "./routes/serveredit";
import PrivateRoute from "./routes/privateroute";

const router = createBrowserRouter([
  {
    path: "/",
    element: (
      <PrivateRoute>
        <Outlet />
      </PrivateRoute>
    ),

    children: [
      {
        path: "contacts/:contactId",
        element: <Hello />,
      },
      {
        path: "/groupchat",
        element: <GroupChat />,
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
        path: "/server/:seq",
        element: <Serverpage />,
      },
      {
        path: "/mainpagecheck",
        element: <Mainpage />,
      },
      {
        path: "/serverenter/:inviteLink",
        element: <ServerEnter />,
      },
      {
        path: "/server/:seq/edit",
        element: <ServerEdit />,
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
        path: "/findid",
        element: <FindId />,
      },
      {
        path: "/findpassword",
        element: <FindPassword />,
      },
    ],
  },
]);

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <Provider store={store}>
      <PersistGate loading={null} persistor={persistor}>
        <RouterProvider router={router} />
      </PersistGate>
    </Provider>
  </React.StrictMode>
);
