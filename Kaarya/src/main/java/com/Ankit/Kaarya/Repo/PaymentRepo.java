package com.Ankit.Kaarya.Repo;

import com.Ankit.Kaarya.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);

    @Query("SELECT p FROM Payment p " +
            "JOIN p.jobApplication ja " +
            "JOIN ja.users u " +
            "WHERE u.userId = :userId")
    List<Payment> findByUserId(@Param("userId") Long userId);




}