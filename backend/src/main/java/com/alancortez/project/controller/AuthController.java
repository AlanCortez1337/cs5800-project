package com.alancortez.project.controller;

import com.alancortez.project.model.User;
import com.alancortez.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> credentials,
            HttpSession session) {

        String userName = credentials.get("userName");
        String password = credentials.get("password");

        User user = userService.getUserByUserName(userName);

        Map<String, Object> response = new HashMap<>();

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getUsername());
            session.setAttribute("userRole", user.getRole().toString());

            response.put("success", true);
            response.put("user", Map.of(
                    "id", user.getId(),
                    "userName", user.getUsername(),
                    "role", user.getRole().toString()
            ));

            return ResponseEntity.ok(response);
        }

        response.put("success", false);
        response.put("message", "Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> checkSession(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");

        Map<String, Object> response = new HashMap<>();

        if (userId != null) {
            response.put("authenticated", true);
            response.put("user", Map.of(
                    "id", userId,
                    "userName", session.getAttribute("userName"),
                    "role", session.getAttribute("userRole")
            ));
            return ResponseEntity.ok(response);
        }

        response.put("authenticated", false);
        return ResponseEntity.ok(response);
    }
}
