package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.ChatMessage;
import com.Ankit.Kaarya.Entity.ChatRoom;
import com.Ankit.Kaarya.Entity.JobApplication;
import com.Ankit.Kaarya.Exceptions.ResourceNotFoundException;
import com.Ankit.Kaarya.Repo.ChatRoomRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RedisTemplate<String, ChatMessage> redisTemplate;
    private final ChatRoomRepo chatRoomRepo;

    public void saveMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        String redisKey = "chat_room:" + message.getRoomId();

        redisTemplate.opsForList().rightPush(redisKey, message);
        redisTemplate.expire(redisKey, 7, TimeUnit.DAYS);
    }

    public List<ChatMessage> getMessageHistory(String roomId) {
        return redisTemplate.opsForList().range("chat_room:" + roomId, 0, -1);
    }

    public ChatRoom createChatRoom(JobApplication application) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setApplication(application);
        return chatRoomRepo.save(chatRoom);
    }

    public ChatRoom getChatRoomByApplicationId(Long applicationId) {
        return chatRoomRepo.findByApplication_JobApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom", "applicationId", applicationId));
    }
}