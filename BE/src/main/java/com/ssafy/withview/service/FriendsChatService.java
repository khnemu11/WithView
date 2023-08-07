package com.ssafy.withview.service;

import com.ssafy.withview.dto.ChannelChatDto;
import com.ssafy.withview.dto.FriendsChatDto;
import com.ssafy.withview.entity.ChannelChatEntity;
import com.ssafy.withview.entity.FriendsChatEntity;
import com.ssafy.withview.repository.FriendsChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendsChatService {

    private final FriendsChatRepository friendsChatRepository;

    public List<FriendsChatDto> getFriendsChatMessagesByPage(Long friendsChatSeq, int page, int cnt) {
        return friendsChatRepository.findByFriendsChatSeqOrderBySendTimeDesc(
                        friendsChatSeq, PageRequest.of(cnt * (page - 1), cnt * page))
                .stream()
                .map(FriendsChatEntity::toDto)
                .collect(Collectors.toList());
    }

    public void insertFriendsChat(FriendsChatDto chat) {
        FriendsChatEntity entity = FriendsChatDto.toEntity(chat);
        friendsChatRepository.save(entity);
    }
}
