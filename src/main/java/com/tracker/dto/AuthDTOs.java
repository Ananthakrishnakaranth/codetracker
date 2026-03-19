package com.tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDTOs {

    public static class RegisterRequest {
        @NotBlank private String name;
        @Email @NotBlank private String email;
        @NotBlank private String password;
        private String department;
        private String rollNumber;
        private String leetcodeUsername;
        private String githubUsername;

        public String getName() { return name; }
        public void setName(String n) { this.name = n; }
        public String getEmail() { return email; }
        public void setEmail(String e) { this.email = e; }
        public String getPassword() { return password; }
        public void setPassword(String p) { this.password = p; }
        public String getDepartment() { return department; }
        public void setDepartment(String d) { this.department = d; }
        public String getRollNumber() { return rollNumber; }
        public void setRollNumber(String r) { this.rollNumber = r; }
        public String getLeetcodeUsername() { return leetcodeUsername; }
        public void setLeetcodeUsername(String l) { this.leetcodeUsername = l; }
        public String getGithubUsername() { return githubUsername; }
        public void setGithubUsername(String g) { this.githubUsername = g; }
    }

    public static class LoginRequest {
        @Email @NotBlank private String email;
        @NotBlank private String password;

        public String getEmail() { return email; }
        public void setEmail(String e) { this.email = e; }
        public String getPassword() { return password; }
        public void setPassword(String p) { this.password = p; }
    }

    public static class AuthResponse {
        private String token;
        private String name;
        private String email;
        private String department;
        private String leetcodeUsername;
        private String githubUsername;

        public AuthResponse(String token, String name, String email,
                            String department, String leetcodeUsername, String githubUsername) {
            this.token = token;
            this.name = name;
            this.email = email;
            this.department = department;
            this.leetcodeUsername = leetcodeUsername;
            this.githubUsername = githubUsername;
        }

        public String getToken() { return token; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getDepartment() { return department; }
        public String getLeetcodeUsername() { return leetcodeUsername; }
        public String getGithubUsername() { return githubUsername; }
    }
}
