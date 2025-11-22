package com.alancortez.project.service;

import com.alancortez.project.model.Admin;
import com.alancortez.project.model.Staff;
import com.alancortez.project.model.User;
import com.alancortez.project.repository.AdminRepository;
import com.alancortez.project.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private StaffRepository staffRepository;


    public User createUser(User user) {
        if (user instanceof Admin) {
            return adminRepository.save((Admin) user);
        } else if (user instanceof Staff) {
            return staffRepository.save((Staff) user);
        }
        throw new IllegalArgumentException("Unknown User type: " + user.getClass().getName());
    }

    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(adminRepository.findAll());
        allUsers.addAll(staffRepository.findAll());
        return allUsers;
    }

    public User getUserById(Integer id) {
        // Try to find in the Staff table
        Optional<Staff> staff = staffRepository.findById(id);
        if (staff.isPresent()) {
            return staff.get();
        }

        // Try to find in the Admin table
        Optional<Admin> admin = adminRepository.findById(id);
        return admin.orElse(null);
    }

    public User getUserByStaffID(String staffID) {
        return staffRepository.findByStaffID(staffID).orElse(null);
    }

    public User getUserByAdminID(String adminID) {
        return adminRepository.findByAdminID(adminID).orElse(null);
    }

    public void deleteUser(Integer id) {
        adminRepository.deleteById(id);
        staffRepository.deleteById(id);
    }

    public User getUserByUserName(String userName) {
        // Try Staff table first
        Optional<Staff> staff = staffRepository.findByUserName(userName);
        if (staff.isPresent()) {
            return staff.get();
        }

        // Try Admin table second
        return adminRepository.findByUserName(userName).orElse(null);
    }
}