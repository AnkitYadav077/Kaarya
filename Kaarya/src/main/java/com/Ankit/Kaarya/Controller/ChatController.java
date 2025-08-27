package com.Ankit.Kaarya.Controller;

import com.Ankit.Kaarya.Entity.ChatMessage;
import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Entity.Users;
import com.Ankit.Kaarya.Repo.IndustryRepo;
import com.Ankit.Kaarya.Repo.UserRepo;
import com.Ankit.Kaarya.Security.OtpAuthenticationToken;
import com.Ankit.Kaarya.Service.ChatService;
import com.Ankit.Kaarya.Service.ChatSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {


    private final SimpMessageSendingOperations messagingTemplate;


    private final ChatService chatService;


    private final UserRepo userRepo;


    private final IndustryRepo industryRepo;


    private final ChatSecurityService chatSecurityService;


    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        if (principal instanceof OtpAuthenticationToken auth) {
            Long senderId = auth.getId();
            String role = auth.getRole(); // role from token
            String displayName = "Unknown";

            if ("ROLE_USER".equalsIgnoreCase(role)) {
                displayName = userRepo.findById(Math.toIntExact(senderId))
                        .map(Users::getName)
                        .orElse("Unknown");
            }
            else if ("ROLE_INDUSTRY".equalsIgnoreCase(role)) {
                displayName = industryRepo.findById(Math.toIntExact(senderId))
                        .map(Industry::getName)
                        .orElse("Unknown");
            }

            chatMessage.setSender(displayName);
            chatMessage.setSenderId(senderId);
        } else {
            chatMessage.setSender("Unknown");
            chatMessage.setSenderId(-1L);
        }

        chatService.saveMessage(chatMessage);
        messagingTemplate.convertAndSend("/topic/" + chatMessage.getRoomId(), chatMessage);
    }





    @GetMapping("/chat/history/{roomId}")
    @PreAuthorize("@chatSecurityService.hasAccessToRoom(#roomId, authentication)")
    public List<ChatMessage> getHistory(@PathVariable String roomId) {
        return chatService.getMessageHistory(roomId);
    }
}