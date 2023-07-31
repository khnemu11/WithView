package com.ssafy.withview.repository;

import com.ssafy.withview.repository.entity.ServerEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ServerRepositoryTest {
    @Autowired
    ServerRepository serverRepository;

    @Test
    void addServer(){
        ServerEntity entity = ServerEntity.builder()
                .name("테스트")
                .hostSeq(1)
                .limitChannel(5)
                .build();

        System.out.println(entity);
        serverRepository.save(entity);
        System.out.println(entity);
    }
}