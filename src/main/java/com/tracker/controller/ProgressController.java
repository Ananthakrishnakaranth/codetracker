package com.tracker.controller;

import com.tracker.dto.ProgressDTOs;
import com.tracker.model.Streak;
import com.tracker.model.User;
import com.tracker.repository.StreakRepository;
import com.tracker.service.ProgressService;
import com.tracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProgressController {

    private final ProgressService progressService;
    private final UserService userService;
    private final StreakRepository streakRepository;

    public ProgressController(ProgressService progressService, UserService userService,
                               StreakRepository streakRepository) {
        this.progressService = progressService;
        this.userService = userService;
        this.streakRepository = streakRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ProgressDTOs.DashboardResponse> getDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(progressService.getDashboard(user));
    }

    @PostMapping("/progress/refresh")
    public ResponseEntity<String> refresh(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        progressService.takeSnapshot(user);
        return ResponseEntity.ok("Progress refreshed successfully");
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<ProgressDTOs.LeaderboardEntry>> getLeaderboard() {
        List<Streak> streaks = streakRepository.findAllOrderByCurrentStreakDesc();
        List<ProgressDTOs.LeaderboardEntry> leaderboard = new ArrayList<>();
        for (Streak streak : streaks) {
            User u = streak.getUser();
            ProgressDTOs.LeaderboardEntry entry = new ProgressDTOs.LeaderboardEntry();
            entry.setUserId(u.getId());
            entry.setName(u.getName());
            entry.setDepartment(u.getDepartment());
            entry.setCurrentStreak(streak.getCurrentStreak());
            entry.setLongestStreak(streak.getLongestStreak());
            entry.setTotalActiveDays(streak.getTotalActiveDays());
            leaderboard.add(entry);
        }
        return ResponseEntity.ok(leaderboard);
    }
}
