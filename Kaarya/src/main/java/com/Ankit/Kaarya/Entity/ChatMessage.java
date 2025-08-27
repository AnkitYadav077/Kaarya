package com.Ankit.Kaarya.Entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private String content;
    private String sender;
    private Long senderId;
    private String roomId;
    private LocalDateTime timestamp;
}