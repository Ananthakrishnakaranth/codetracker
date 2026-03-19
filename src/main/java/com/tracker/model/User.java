package com.tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    private String password;

    private String department;
    private String rollNumber;
    private String leetcodeUsername;
    private String githubUsername;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    public enum Role { USER, ADMIN }

    public Long getId() { return id; }
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
    public Role getRole() { return role; }
    public void setRole(Role r) { this.role = r; }
}
