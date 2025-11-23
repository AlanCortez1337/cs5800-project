package com.alancortez.project.repository;

import com.alancortez.project.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByAdminID(String adminID);
    Optional<Admin> findByUserName(String userName); // Added username lookup
}
