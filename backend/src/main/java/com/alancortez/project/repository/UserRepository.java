package com.alancortez.project.repository;

import com.alancortez.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA automatically implements this method
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}