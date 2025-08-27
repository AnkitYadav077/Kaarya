package com.Ankit.Kaarya.Config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {


    @PostConstruct
    public void init() {
        Twilio.init(
                "AC831b61d3ef68d8cfd1b86ef82f379fe3",
                "d6b52b52dff0538b43cabf6f78cbfaa3"
        );
        System.out.println("Twilio initialized successfully!");
    }
}