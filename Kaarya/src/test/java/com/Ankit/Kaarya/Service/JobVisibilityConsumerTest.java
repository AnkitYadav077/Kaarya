package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.JobVisibilityEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JobVisibilityConsumerTest {

    @InjectMocks
    private JobVisibilityConsumer jobVisibilityConsumer;

    @Test
    void listen_VisibleEvent_AddsJobId() {
        // Arrange
        JobVisibilityEvent event = new JobVisibilityEvent(1L, true);
        ConsumerRecord<String, JobVisibilityEvent> record =
                new ConsumerRecord<>("topic", 0, 0, "key", event);

        // Act
        jobVisibilityConsumer.listen(record);

        // Assert
        assertTrue(jobVisibilityConsumer.isJobVisible(1L));
    }

    @Test
    void listen_InvisibleEvent_RemovesJobId() {
        // Arrange
        JobVisibilityEvent event1 = new JobVisibilityEvent(1L, true);
        JobVisibilityEvent event2 = new JobVisibilityEvent(1L, false);

        ConsumerRecord<String, JobVisibilityEvent> record1 =
                new ConsumerRecord<>("topic", 0, 0, "key", event1);
        ConsumerRecord<String, JobVisibilityEvent> record2 =
                new ConsumerRecord<>("topic", 0, 0, "key", event2);

        // Act
        jobVisibilityConsumer.listen(record1);
        jobVisibilityConsumer.listen(record2);

        // Assert
        assertFalse(jobVisibilityConsumer.isJobVisible(1L));
    }

    @Test
    void getAllVisibleJobs_ReturnsVisibleJobs() {
        // Arrange
        JobVisibilityEvent event1 = new JobVisibilityEvent(1L, true);
        JobVisibilityEvent event2 = new JobVisibilityEvent(2L, true);

        ConsumerRecord<String, JobVisibilityEvent> record1 =
                new ConsumerRecord<>("topic", 0, 0, "key", event1);
        ConsumerRecord<String, JobVisibilityEvent> record2 =
                new ConsumerRecord<>("topic", 0, 0, "key", event2);

        // Act
        jobVisibilityConsumer.listen(record1);
        jobVisibilityConsumer.listen(record2);
        Set<Long> visibleJobs = jobVisibilityConsumer.getAllVisibleJobs();

        // Assert
        assertEquals(2, visibleJobs.size());
        assertTrue(visibleJobs.contains(1L));
        assertTrue(visibleJobs.contains(2L));
    }
}