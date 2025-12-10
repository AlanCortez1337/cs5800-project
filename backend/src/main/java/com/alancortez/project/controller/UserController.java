package com.alancortez.project.controller;

import com.alancortez.project.model.User;
import com.alancortez.project.service.UserService;
import com.alancortez.project.utils.PRIVILEGES;
import com.alancortez.project.utils.USER_ROLE;
import com.alancortez.project.utils.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // GET /apiuser/{userName}
    @GetMapping("/{userName}")
    public ResponseEntity<User> getUserByUserName(@PathVariable String userName) {
        User user = userService.getUserByUserName(userName);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // GET /apiuser/staff/{id}
    @GetMapping("/staff/{id}")
    public ResponseEntity<User> getUserByStaffID(@PathVariable String staffID) {
        User user = userService.getUserByStaffID(staffID);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // GET /apiuser/staff/{id}
    @GetMapping("/admin/{id}")
    public ResponseEntity<User> getUserByAdminID(@PathVariable String adminID) {
        User user = userService.getUserByAdminID(adminID);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // POST /apiuser
    @PostMapping
    public User createUser(@RequestBody Map<String, String> request) {
        UserFactory userFactory = UserFactory.getInstance();

        String userName = request.get("userName");
        String password = request.get("password");
        USER_ROLE role = USER_ROLE.valueOf(request.get("role"));

        return userService.createUser(userFactory.createUser(userName, password, role));
    }

    // PUT /apiuser/{id}
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        User user = userService.getUserById(id);
        if (user != null) {
            String username = request.get("username");
            String password = request.get("password");

            if (username != null) {
                user.setUsername(username);
            }
            if (password != null && !password.isEmpty()) {
                user.setPassword(password);
            }
            return ResponseEntity.ok(userService.createUser(user));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/privilege/{adminID}/{staffID}")
    public ResponseEntity<String> updateStaffPrivileges(
            @PathVariable String adminID,
            @PathVariable String staffID,
            @RequestBody String[] privilegesToToggle
    ) {
        // Map the array of privilege strings from the request body to the PRIVILEGES enum array
        PRIVILEGES[] privilegeEnums;
        try {
            privilegeEnums = new PRIVILEGES[privilegesToToggle.length];
            for (int i = 0; i < privilegesToToggle.length; i++) {
                privilegeEnums[i] = PRIVILEGES.valueOf(privilegesToToggle[i].toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            // Handle case where an invalid privilege name is sent
            return new ResponseEntity<>("Invalid privilege name provided.", HttpStatus.BAD_REQUEST);
        }

        try {
            userService.changeStaffPrivilege(adminID, staffID, privilegeEnums);
            return new ResponseEntity<>("Staff privileges updated successfully.", HttpStatus.OK);
        } catch (RuntimeException e) {
            // Catch specific exceptions from the service layer (e.g., UserNotFoundException)
            // For now, using a generic catch to demonstrate the service call.
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Failed to update privileges: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE /apiuser/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}