package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.JobVisibilityEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JobVisibilityConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JobVisibilityConsumer.class);
    private final Set<Long> visibleJobIds = ConcurrentHashMap.newKeySet();

    @KafkaListener(topics = "${app.kafka.job-visibility-topic}", groupId = "job-visibility-group")
    public void listen(ConsumerRecord<String, JobVisibilityEvent> record) {
        JobVisibilityEvent event = record.value();

        logger.info("Received visibility event: topic={}, partition={}, offset={}, key={}, value={}",
                record.topic(), record.partition(), record.offset(), record.key(), event);

        if (event.isVisible()) {
            visibleJobIds.add(event.getJobId());
            logger.debug("Job ID {} is now visible", event.getJobId());
        } else {
            visibleJobIds.remove(event.getJobId());
            logger.debug("Job ID {} is now invisible", event.getJobId());
        }
    }

    public boolean isJobVisible(Long jobId) {
        return visibleJobIds.contains(jobId);
    }

    public Set<Long> getAllVisibleJobs() {
        return visibleJobIds;
    }
}