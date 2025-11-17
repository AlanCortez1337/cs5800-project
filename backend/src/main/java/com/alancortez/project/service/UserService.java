package com.alancortez.project.service;

import com.alancortez.project.model.User;
import com.alancortez.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByStaffID(Long id) {
        return userRepository.findByStaffID(id).orElse(null);
    }

    public User getUserByAdminID(Long id) {
        return userRepository.findByAdminID(id).orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User getUserByUsername(String userName) {
        return userRepository.findByUserName(userName).orElse(null);
    }
}