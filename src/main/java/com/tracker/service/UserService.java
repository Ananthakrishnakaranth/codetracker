package com.tracker.service;

import com.tracker.dto.AuthDTOs;
import com.tracker.model.User;
import com.tracker.repository.UserRepository;
import com.tracker.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthDTOs.AuthResponse register(AuthDTOs.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDepartment(request.getDepartment());
        user.setRollNumber(request.getRollNumber());
        user.setLeetcodeUsername(request.getLeetcodeUsername());
        user.setGithubUsername(request.getGithubUsername());
        user.setRole(User.Role.USER);
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthDTOs.AuthResponse(token, user.getName(), user.getEmail(),
                user.getDepartment(), user.getLeetcodeUsername(), user.getGithubUsername());
    }

    public AuthDTOs.AuthResponse login(AuthDTOs.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthDTOs.AuthResponse(token, user.getName(), user.getEmail(),
                user.getDepartment(), user.getLeetcodeUsername(), user.getGithubUsername());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public List<User> searchUsers(String query) {
        return userRepository.searchByNameOrDepartment(query);
    }

    public User updateProfile(String email, AuthDTOs.RegisterRequest request) {
        User user = getUserByEmail(email);
        user.setName(request.getName());
        user.setDepartment(request.getDepartment());
        user.setLeetcodeUsername(request.getLeetcodeUsername());
        user.setGithubUsername(request.getGithubUsername());
        return userRepository.save(user);
    }
}
