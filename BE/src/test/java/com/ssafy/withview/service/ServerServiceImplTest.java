package com.ssafy.withview.service;

import com.ssafy.withview.repository.ChannelRepository;
import com.ssafy.withview.repository.ServerRepository;
import com.ssafy.withview.repository.UserRepository;
import com.ssafy.withview.repository.UserServerRepository;
import com.ssafy.withview.repository.dto.ServerDto;
import com.ssafy.withview.repository.entity.ServerEntity;
import com.ssafy.withview.repository.entity.UserServerEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ServerServiceImplTest {
    @Autowired
    private  ServerRepository serverRepository;
    @Autowired
    private  ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServerRepository userServerRepository;
    @Test
    void findAllServerByUserSeq() {
        List<UserServerEntity> userServerEntityList= userServerRepository.findAllServerByUserEntity(userRepository.findBySeq(4));
        List<ServerDto> userServerDtoList = new ArrayList<>();
        System.out.println("찾은 유저의 서버");
        System.out.println(userServerEntityList);
        
        for(UserServerEntity userServerEntity : userServerEntityList){
            userServerDtoList.add(ServerEntity.toDto(userServerEntity.getServerEntity()));
        }



        System.out.println(userServerDtoList);
    }
    @Test
    void findAllServerByUserSeqEmpty() {
        List<UserServerEntity> userServerEntityList= userServerRepository.findAllServerByUserEntity(userRepository.findBySeq(0));
        List<ServerDto> userServerDtoList = new ArrayList<>();
        System.out.println("찾은 유저의 서버");
        System.out.println(userServerEntityList);

        for(UserServerEntity userServerEntity : userServerEntityList){
            userServerDtoList.add(ServerEntity.toDto(userServerEntity.getServerEntity()));
        }

        System.out.println(userServerDtoList);
    }
}