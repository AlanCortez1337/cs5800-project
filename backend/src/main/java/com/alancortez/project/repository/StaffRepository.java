package com.alancortez.project.repository;

import com.alancortez.project.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    Optional<Staff> findByStaffID(String staffID);
    Optional<Staff> findByUserName(String userName);
}
