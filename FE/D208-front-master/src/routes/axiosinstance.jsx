// src/routes/axiosinstance.jsx
import axios from 'axios';
import { setToken } from '../redux/actions/tokenActions';
import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { clearUser} from '../redux/actions/userActions';
import { clearToken } from '../redux/actions/tokenActions';
import { Navigate } from 'react-router-dom';

const axiosInstance = axios.create({
  baseURL: 'https://i9d208.p.ssafy.io/api', // 기본 URL 설정
});

 
 function AxiosInterceptor({children}) {
    const dispatch = useDispatch();
    useEffect(()=>{
        axiosInstance.interceptors.response.use(
            (response) => response,
            async (error) => {
              const originalRequest = error.config;
          
              if (error.response.data.message === "REISSUE_ACCESS_TOKEN") {
                // originalRequest._retry = true;
                const newAccessToken = error.response.headers['new-access-token'];
          
                if (newAccessToken) {
                  dispatch(setToken(newAccessToken));
          
                  originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
                  return axiosInstance(originalRequest);
                }
              }
              else if (error.response.data.message === "EXPIRED_REFRESH_TOKEN") {
                dispatch(clearToken())
                dispatch(clearUser())
                alert('토큰이 만료되어 로그아웃 합니다.')
                return <Navigate to="/login" />;
              }


              return Promise.reject(error);
            }
          );
    },[])
    
    
    return children;
 }
 
 export default axiosInstance;
 export {AxiosInterceptor}


// axiosInstance.interceptors.request.use(
//   async (config) => {
//     if (accessToken) {
//       config.headers['Authorization'] = `Bearer ${accessToken}`;
//     }
//     return config;
//   },
//   (error) => {
//     return Promise.reject(error);
//   }
// );



