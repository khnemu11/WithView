package com.ssafy.withview.repository;

import com.ssafy.withview.repository.dto.UserDto;
import com.ssafy.withview.repository.entity.ServerEntity;
import com.ssafy.withview.repository.entity.UserEntity;
import com.ssafy.withview.repository.entity.UserServerEntity;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServerRepositoryTest {
    @Autowired
    UserServerRepository userServerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ServerRepository serverRepository;
    @Test
    @Transactional
    void addServer(){
        UserEntity userEntity = userRepository.findBySeq(4);
        System.out.println(userEntity);

        System.out.println("===== 서버 목록 =====");
        System.out.println(serverRepository.findAll());
        System.out.println("===== 유저/서버 목록 =====");
        System.out.println(userServerRepository.findAll());

        List<UserServerEntity> userServerEntityList = new ArrayList<>();
        userServerEntityList.add(UserServerEntity.builder()
                .userEntity(userEntity)
                .serverEntity(serverRepository.findBySeq(1))
                .build());
        userServerEntityList.add(UserServerEntity.builder()
                .userEntity(userEntity)
                .serverEntity(serverRepository.findBySeq(1))
                .build());
        userServerEntityList.add(UserServerEntity.builder()
                .userEntity(userEntity)
                .serverEntity(serverRepository.findBySeq(1))
                .build());

        userServerRepository.saveAll(userServerEntityList);

        System.out.println("===== 넣은 후 유저/서버 ======");
        System.out.println(userServerEntityList);
        System.out.println("===== 넣은 후 유저 ======");
        userEntity = userRepository.findBySeq(4);
        System.out.println(userEntity);
        System.out.println(userEntity.getServers());
    }

    @Test
    void seqTest(){
        UserDto userDto = UserDto.builder()
                .seq(9999)
                .nickname("seqtest")
                .realName("김수현")
                .build();
        System.out.println(userDto);
        UserEntity userEntity = userRepository.save(UserDto.toEntity(userDto));
        System.out.println(userEntity);
        System.out.println(userRepository.findBySeq(9999));
    }
}