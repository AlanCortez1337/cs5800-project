package com.alancortez.project.repository;

import com.alancortez.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByStaffID(Long staffID);
    Optional<User> findByAdminID(Long staffID);
}