package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.ChatMessage;
import com.Ankit.Kaarya.Entity.ChatRoom;
import com.Ankit.Kaarya.Entity.JobApplication;
import com.Ankit.Kaarya.Exceptions.ResourceNotFoundException;
import com.Ankit.Kaarya.Repo.ChatRoomRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ListOperations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private RedisTemplate<String, ChatMessage> redisTemplate;

    @Mock
    private ListOperations<String, ChatMessage> listOperations;

    @Mock
    private ChatRoomRepo chatRoomRepo;

    @InjectMocks
    private ChatService chatService;

    @Test
    void saveMessage_Success() {
        // Arrange
        ChatMessage message = new ChatMessage();
        message.setRoomId("room1");
        when(redisTemplate.opsForList()).thenReturn(listOperations);

        // Act
        chatService.saveMessage(message);

        // Assert
        verify(listOperations).rightPush(anyString(), any(ChatMessage.class));
        verify(redisTemplate).expire(anyString(), anyLong(), any());
        assertNotNull(message.getTimestamp());
    }

    @Test
    void getMessageHistory_ReturnsMessages() {
        // Arrange
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.range(anyString(), anyLong(), anyLong()))
                .thenReturn(Arrays.asList(new ChatMessage(), new ChatMessage()));

        // Act
        List<ChatMessage> result = chatService.getMessageHistory("room1");

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void createChatRoom_Success() {
        // Arrange
        JobApplication application = new JobApplication();
        ChatRoom chatRoom = new ChatRoom();
        when(chatRoomRepo.save(any())).thenReturn(chatRoom);

        // Act
        ChatRoom result = chatService.createChatRoom(application);

        // Assert
        assertNotNull(result);
        verify(chatRoomRepo).save(any(ChatRoom.class));
    }

    @Test
    void getChatRoomByApplicationId_Exists_ReturnsChatRoom() {
        // Arrange
        ChatRoom chatRoom = new ChatRoom();
        when(chatRoomRepo.findByApplication_JobApplicationId(anyLong()))
                .thenReturn(Optional.of(chatRoom));

        // Act
        ChatRoom result = chatService.getChatRoomByApplicationId(1L);

        // Assert
        assertNotNull(result);
    }

    @Test
    void getChatRoomByApplicationId_NotFound_ThrowsException() {
        // Arrange
        when(chatRoomRepo.findByApplication_JobApplicationId(anyLong()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            chatService.getChatRoomByApplicationId(1L);
        });
    }
}