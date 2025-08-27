package com.Ankit.Kaarya.Repo;

import com.Ankit.Kaarya.Entity.ApplicationStatus;
import com.Ankit.Kaarya.Entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepo extends JpaRepository<JobApplication, Long> {


    boolean existsByUsersUserIdAndStatusIn(
            Long userId, List<ApplicationStatus> statuses);


    @Query("SELECT COUNT(j) FROM JobApplication j " +
            "WHERE j.jobs.jobId = :jobId AND j.status = 'APPROVED'")
    int countApprovedApplicationsByJobId(@Param("jobId") Long jobId);


    List<JobApplication> findByJobsJobIdAndStatus(Long jobId, ApplicationStatus status);


    @Query("SELECT ja FROM JobApplication ja WHERE ja.jobs.industry.industryId = :industryId")
    List<JobApplication> findByJobs_JobId(@Param("industryId") Long industryId);


    List<JobApplication> findByUsers_UserId(Integer userId);


    List<JobApplication> findByStatus(ApplicationStatus status);


    long countByUsers_UserId(Integer userId);
    long countByUsers_UserIdAndStatus(Integer userId, ApplicationStatus status);
    long countByUsers_UserIdAndStatusIn(Integer userId, List<ApplicationStatus> statuses);


    @Query("SELECT ja FROM JobApplication ja " +
            "JOIN FETCH ja.jobs j " +
            "JOIN FETCH j.industry " +
            "WHERE ja.users.userId = :userId")
    List<JobApplication> findByUserIdWithJobAndIndustry(@Param("userId") Integer userId);

    @Query("SELECT ja FROM JobApplication ja " +
            "WHERE ja.jobs.industry.industryId = :industryId " +
            "AND ja.status = 'APPROVED'")
    List<JobApplication> findApprovedApplicationsByIndustry(@Param("industryId") Long industryId);

    List<JobApplication> findByUsers_UserIdAndStatus(Long userId, ApplicationStatus status);

}