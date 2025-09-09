package com.Ankit.Kaarya.Repo;

import com.Ankit.Kaarya.Entity.JobApplication;
import com.Ankit.Kaarya.Entity.Users;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users,Integer> {

    Optional<Users> findByPhoneNo(String phoneNo);


    boolean existsByPhoneNo(String phoneNo);
}
