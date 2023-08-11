import { Navigate, useLocation } from "react-router-dom";
import { useSelector } from "react-redux";

function PrivateRoute({ children }) {
  const location = useLocation();
  const isLoggedIn = useSelector((state) => state.token);
  const excludedPaths = ["/signup", "/findid", "/findpassword","/login"];

  if (!isLoggedIn && !excludedPaths.includes(location.pathname)) {
    return <Navigate to="/login" />;
  } else if (isLoggedIn && (location.pathname === "/" || location.pathname === "/login" || location.pathname === "/signup" || location.pathname === "/findid" || location.pathname === "/findpassword")) {
    return <Navigate to="/mainpage" />;
  }
  return children;
}

export default PrivateRoute;
