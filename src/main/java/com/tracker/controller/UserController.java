package com.tracker.controller;

import com.tracker.dto.AuthDTOs;
import com.tracker.model.User;
import com.tracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("name", user.getName());
        profile.put("email", user.getEmail());
        profile.put("department", user.getDepartment() != null ? user.getDepartment() : "");
        profile.put("rollNumber", user.getRollNumber() != null ? user.getRollNumber() : "");
        profile.put("leetcodeUsername", user.getLeetcodeUsername() != null ? user.getLeetcodeUsername() : "");
        profile.put("githubUsername", user.getGithubUsername() != null ? user.getGithubUsername() : "");
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, String>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AuthDTOs.RegisterRequest request) {
        userService.updateProfile(userDetails.getUsername(), request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Profile updated successfully");
        return ResponseEntity.ok(response);
    }
}
