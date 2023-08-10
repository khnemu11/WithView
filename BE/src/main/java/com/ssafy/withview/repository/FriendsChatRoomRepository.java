package com.ssafy.withview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.withview.entity.FriendsChatRoomEntity;

@Repository
public interface FriendsChatRoomRepository extends JpaRepository<FriendsChatRoomEntity, String> {

}
