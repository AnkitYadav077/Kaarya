package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.JobVisibilityEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobVisibilityProducer {

    @Value("${app.kafka.job-visibility-topic}")
    private String topic;

    private final KafkaTemplate<String, JobVisibilityEvent> kafkaTemplate;



    public void sendVisibilityEvent(JobVisibilityEvent event) {
        kafkaTemplate.send(topic, event);
    }
}