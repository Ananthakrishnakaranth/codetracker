package com.tracker.service;

import com.tracker.dto.ProgressDTOs;
import com.tracker.model.ProgressSnapshot;
import com.tracker.model.Streak;
import com.tracker.model.User;
import com.tracker.repository.ProgressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProgressService {

    private static final Logger log = LoggerFactory.getLogger(ProgressService.class);

    private final ProgressRepository progressRepository;
    private final LeetCodeService leetCodeService;
    private final GitHubService gitHubService;
    private final StreakService streakService;

    public ProgressService(ProgressRepository progressRepository,
                           LeetCodeService leetCodeService,
                           GitHubService gitHubService,
                           StreakService streakService) {
        this.progressRepository = progressRepository;
        this.leetCodeService = leetCodeService;
        this.gitHubService = gitHubService;
        this.streakService = streakService;
    }

    @Transactional
    public ProgressSnapshot takeSnapshot(User user) {
        LocalDate today = LocalDate.now();
        Optional<ProgressSnapshot> existing = progressRepository.findByUserAndDate(user, today);
        if (existing.isPresent()) {
            log.info("Snapshot already exists for user {} on {}", user.getId(), today);
            return existing.get();
        }

        LeetCodeService.LeetCodeStats lcStats = leetCodeService.fetchStats(user.getLeetcodeUsername());
        int githubCommits = gitHubService.fetchTodayCommitCount(user.getGithubUsername());

        Optional<ProgressSnapshot> yesterday = progressRepository.findByUserAndDate(user, today.minusDays(1));
        int prevTotal = yesterday.map(ProgressSnapshot::getLeetcodeTotalSolved).orElse(0);
        int newSolvedToday = Math.max(0, lcStats.getTotalSolved() - prevTotal);
        boolean activeToday = newSolvedToday > 0 || githubCommits > 0;

        ProgressSnapshot snapshot = new ProgressSnapshot();
        snapshot.setUser(user);
        snapshot.setDate(today);
        snapshot.setLeetcodeTotalSolved(lcStats.getTotalSolved());
        snapshot.setLeetcodeEasySolved(lcStats.getEasySolved());
        snapshot.setLeetcodeMediumSolved(lcStats.getMediumSolved());
        snapshot.setLeetcodeHardSolved(lcStats.getHardSolved());
        snapshot.setLeetcodeNewSolvedToday(newSolvedToday);
        snapshot.setGithubCommitsToday(githubCommits);
        snapshot.setActiveDay(activeToday);

        progressRepository.save(snapshot);
        streakService.updateStreak(user, activeToday);

        log.info("Snapshot saved for user {}: LC={}, GH={}, active={}",
                user.getId(), lcStats.getTotalSolved(), githubCommits, activeToday);
        return snapshot;
    }

    public ProgressDTOs.DashboardResponse getDashboard(User user) {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);

        Optional<ProgressSnapshot> todaySnapshot = progressRepository.findByUserAndDate(user, today);
        Streak streak = streakService.getOrCreate(user);

        ProgressSnapshot current = todaySnapshot.orElseGet(() -> {
            try { return takeSnapshot(user); }
            catch (Exception e) {
                log.warn("Could not take live snapshot: {}", e.getMessage());
                return progressRepository.findTopByUserOrderByDateDesc(user).orElse(null);
            }
        });

        List<ProgressSnapshot> history = progressRepository.findRecentByUser(user, thirtyDaysAgo);
        List<ProgressDTOs.SnapshotSummary> summaries = new ArrayList<>();
        for (ProgressSnapshot s : history) {
            ProgressDTOs.SnapshotSummary summary = new ProgressDTOs.SnapshotSummary();
            summary.setDate(s.getDate());
            summary.setLeetcodeNewSolved(s.getLeetcodeNewSolvedToday());
            summary.setGithubCommits(s.getGithubCommitsToday());
            summary.setActiveDay(s.isActiveDay());
            summaries.add(summary);
        }

        ProgressDTOs.DashboardResponse resp = new ProgressDTOs.DashboardResponse();
        resp.setName(user.getName());
        resp.setLeetcodeUsername(user.getLeetcodeUsername());
        resp.setGithubUsername(user.getGithubUsername());
        resp.setLeetcodeTotalSolved(current != null ? current.getLeetcodeTotalSolved() : 0);
        resp.setLeetcodeEasySolved(current != null ? current.getLeetcodeEasySolved() : 0);
        resp.setLeetcodeMediumSolved(current != null ? current.getLeetcodeMediumSolved() : 0);
        resp.setLeetcodeHardSolved(current != null ? current.getLeetcodeHardSolved() : 0);
        resp.setGithubCommitsToday(current != null ? current.getGithubCommitsToday() : 0);
        resp.setCurrentStreak(streak.getCurrentStreak());
        resp.setLongestStreak(streak.getLongestStreak());
        resp.setTotalActiveDays(streak.getTotalActiveDays());
        resp.setActiveToday(current != null && current.isActiveDay());
        resp.setRecentHistory(summaries);
        return resp;
    }
}
