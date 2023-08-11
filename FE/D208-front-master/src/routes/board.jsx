import React from 'react'
import ServerOptions from './components/serveroptions'
import { useSelector } from 'react-redux';
import { useEffect,useState } from 'react';

function Board() {
    
    const profileNickname = useSelector((state) => state.user.nickname);
    const [profileImage, setProfileImage] = useState(null);
    const profileImageURL = useSelector((state) => state.user.profile);
    const profileImageUrl = `https://dm51j1y1p1ekp.cloudfront.net/profile/${profileImageURL}`;
    

    useEffect(() => {
        // 만약 redux에서 프로필 이미지가 null이면 기본 이미지로 설정
        if (profileImageURL === null) {
          setProfileImage("/withView2.png");
        } else {
          setProfileImage(profileImageUrl);
        }
      }, [profileImageURL]);
    
    return (
    
    <div className="innermain">
        <ServerOptions
            profileImage={profileImage}
            profileNickname={profileNickname}
        />
    
        <hr className="serverOptionsLine_profile" />

        
    </div>
  )
}

export default Board