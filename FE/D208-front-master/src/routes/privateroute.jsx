import { Navigate, useLocation } from "react-router-dom";
import { useSelector } from "react-redux";

function PrivateRoute({ children }) {
  const location = useLocation();
  const isLoggedIn = useSelector((state) => state.token);
  const excludedPaths = ["/signup", "/findid", "/findpassword","/login"];
  // 페이지가 로드될 때 실행되는 코드
//   window.addEventListener('beforeunload', function(event) {
//   // 로컬 스토리지 비우기
//   localStorage.clear();
  
//   // 정확한 메시지를 표시하기 위한 코드
//   event.returnValue = '이 페이지를 떠날 때 로컬 데이터가 모두 삭제됩니다.';
// });

  if (!isLoggedIn && !excludedPaths.includes(location.pathname)) {
    return <Navigate to="/login" />;
  } else if (isLoggedIn && (location.pathname === "/" || location.pathname === "/login" || location.pathname === "/signup" || location.pathname === "/findid" || location.pathname === "/findpassword")) {
    return <Navigate to="/mainpage" />;
  }
  return children;
}

export default PrivateRoute;
