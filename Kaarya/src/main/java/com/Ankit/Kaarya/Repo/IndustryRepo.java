package com.Ankit.Kaarya.Repo;

import com.Ankit.Kaarya.Entity.Industry;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IndustryRepo extends JpaRepository<Industry,Integer> {

    Optional<Industry> findByEmail(String email);

    boolean existsByEmail(String email);
}
