package com.Ankit.Kaarya.Config;

import com.Ankit.Kaarya.Entity.JobVisibilityEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobWebSocketBroadcaster {


    private final SimpMessagingTemplate messagingTemplate;



    public void broadcast(JobVisibilityEvent event) {
        messagingTemplate.convertAndSend("/topic/job-visibility", event);
    }




}