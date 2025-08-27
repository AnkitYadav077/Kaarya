package com.Ankit.Kaarya.Repo;

import com.Ankit.Kaarya.Entity.ChatRoom;
import com.Ankit.Kaarya.Entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChatRoomRepo extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByApplication(JobApplication application);
    Optional<ChatRoom> findByRoomId(String roomId);
    Optional<ChatRoom> findByApplication_JobApplicationId(Long jobApplicationId);

}