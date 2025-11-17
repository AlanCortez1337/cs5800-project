package com.alancortez.project.controller;

import com.alancortez.project.model.User;
import com.alancortez.project.service.UserService;
import com.alancortez.project.utils.USER_ROLE;
import com.alancortez.project.utils.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // GET /apiuser
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET /apiuser/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // GET /apiuser/{userName}
    @GetMapping("/{userName}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String userName) {
        User user = userService.getUserByUsername(userName);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // GET /apiuser/staff/{id}
    @GetMapping("/staff/{id}")
    public ResponseEntity<User> getUserByStaffID(@PathVariable Long id) {
        User user = userService.getUserByStaffID(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // GET /apiuser/staff/{id}
    @GetMapping("/admin/{id}")
    public ResponseEntity<User> getUserByAdminID(@PathVariable Long id) {
        User user = userService.getUserByAdminID(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // POST /apiuser
    @PostMapping
    public User createUser(@RequestBody String userName, String password, @RequestBody USER_ROLE role) {
        UserFactory userFactory = UserFactory.getInstance();

        return userService.createUser(userFactory.createUser(userName, password, role));
    }

    // PUT /apiuser/{id}
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userService.getUserById(id);
        if (user != null) {
            user.setUsername(userDetails.getUsername());
            user.setPassword(userDetails.getPassword());
            return ResponseEntity.ok(userService.createUser(user));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /apiuser/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}