package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Config.JobWebSocketBroadcaster;
import com.Ankit.Kaarya.Entity.JobVisibilityEvent;
import com.Ankit.Kaarya.Entity.Jobs;
import com.Ankit.Kaarya.Repo.JobRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JobVisibilityScheduler {

    private final JobRepo jobRepo;
    private final JobVisibilityProducer producer;
    private final JobWebSocketBroadcaster broadcaster;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkAndBroadcastVisibility() {
        LocalDateTime now = LocalDateTime.now();
        List<Jobs> jobs = jobRepo.findAll();

        for (Jobs job : jobs) {
            boolean shouldBeVisible = now.isAfter(job.getWorkDate().minusDays(7)) &&
                    now.isBefore(job.getWorkDate());

            boolean isExpired = now.isAfter(job.getWorkDate());
            boolean isFull = job.getJobs().size() >= job.getRequiredWorkers();

            boolean visible = shouldBeVisible && !isExpired && !isFull;

            JobVisibilityEvent event = new JobVisibilityEvent(job.getJobId(), visible);
            producer.sendVisibilityEvent(event);
            broadcaster.broadcast(event);
        }
    }
}