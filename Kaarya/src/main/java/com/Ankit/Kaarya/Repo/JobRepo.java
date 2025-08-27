package com.Ankit.Kaarya.Repo;

import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Entity.Jobs;
import com.Ankit.Kaarya.Payloads.JobsDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface JobRepo extends JpaRepository<Jobs, Integer>, JpaSpecificationExecutor<Jobs> {


    List<Jobs> findByWorkDateBetween(LocalDateTime start, LocalDateTime end);


    List<Jobs> findByIndustry(Industry industry);


    default List<Jobs> findFilteredJobs(String title, LocalDateTime workDate, List<Long> industryIds) {
        Specification<Jobs> spec = Specification.where(null);

        if (title != null && !title.isEmpty()) {
            spec = spec.and(titleContains(title));
        }

        if (workDate != null) {
            spec = spec.and(workDateEquals(workDate));
        }

        if (industryIds != null && !industryIds.isEmpty()) {
            spec = spec.and(industryIn(industryIds));
        }

        return findAll(spec);
    }

    static Specification<Jobs> titleContains(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    static Specification<Jobs> workDateEquals(LocalDateTime workDate) {
        return (root, query, cb) -> {
            LocalDateTime start = workDate.toLocalDate().atStartOfDay();
            LocalDateTime end = workDate.toLocalDate().atTime(23, 59, 59);
            return cb.between(root.get("workDate"), start, end);
        };
    }

    static Specification<Jobs> industryIn(List<Long> industryIds) {
        return (root, query, cb) -> root.get("industry").get("industryId").in(industryIds);
    }
}