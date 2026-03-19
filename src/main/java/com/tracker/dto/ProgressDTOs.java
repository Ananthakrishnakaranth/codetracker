package com.tracker.dto;

import java.time.LocalDate;
import java.util.List;

public class ProgressDTOs {

    public static class DashboardResponse {
        private String name;
        private String leetcodeUsername;
        private String githubUsername;
        private int leetcodeTotalSolved;
        private int leetcodeEasySolved;
        private int leetcodeMediumSolved;
        private int leetcodeHardSolved;
        private int githubCommitsToday;
        private int currentStreak;
        private int longestStreak;
        private int totalActiveDays;
        private boolean activeToday;
        private List<SnapshotSummary> recentHistory;

        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public String getLeetcodeUsername() { return leetcodeUsername; }
        public void setLeetcodeUsername(String v) { this.leetcodeUsername = v; }
        public String getGithubUsername() { return githubUsername; }
        public void setGithubUsername(String v) { this.githubUsername = v; }
        public int getLeetcodeTotalSolved() { return leetcodeTotalSolved; }
        public void setLeetcodeTotalSolved(int v) { this.leetcodeTotalSolved = v; }
        public int getLeetcodeEasySolved() { return leetcodeEasySolved; }
        public void setLeetcodeEasySolved(int v) { this.leetcodeEasySolved = v; }
        public int getLeetcodeMediumSolved() { return leetcodeMediumSolved; }
        public void setLeetcodeMediumSolved(int v) { this.leetcodeMediumSolved = v; }
        public int getLeetcodeHardSolved() { return leetcodeHardSolved; }
        public void setLeetcodeHardSolved(int v) { this.leetcodeHardSolved = v; }
        public int getGithubCommitsToday() { return githubCommitsToday; }
        public void setGithubCommitsToday(int v) { this.githubCommitsToday = v; }
        public int getCurrentStreak() { return currentStreak; }
        public void setCurrentStreak(int v) { this.currentStreak = v; }
        public int getLongestStreak() { return longestStreak; }
        public void setLongestStreak(int v) { this.longestStreak = v; }
        public int getTotalActiveDays() { return totalActiveDays; }
        public void setTotalActiveDays(int v) { this.totalActiveDays = v; }
        public boolean isActiveToday() { return activeToday; }
        public void setActiveToday(boolean v) { this.activeToday = v; }
        public List<SnapshotSummary> getRecentHistory() { return recentHistory; }
        public void setRecentHistory(List<SnapshotSummary> v) { this.recentHistory = v; }
    }

    public static class SnapshotSummary {
        private LocalDate date;
        private int leetcodeNewSolved;
        private int githubCommits;
        private boolean activeDay;

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate v) { this.date = v; }
        public int getLeetcodeNewSolved() { return leetcodeNewSolved; }
        public void setLeetcodeNewSolved(int v) { this.leetcodeNewSolved = v; }
        public int getGithubCommits() { return githubCommits; }
        public void setGithubCommits(int v) { this.githubCommits = v; }
        public boolean isActiveDay() { return activeDay; }
        public void setActiveDay(boolean v) { this.activeDay = v; }
    }

    public static class LeaderboardEntry {
        private Long userId;
        private String name;
        private String department;
        private int currentStreak;
        private int longestStreak;
        private int totalActiveDays;
        private int totalLeetcodeSolved;

        public Long getUserId() { return userId; }
        public void setUserId(Long v) { this.userId = v; }
        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public String getDepartment() { return department; }
        public void setDepartment(String v) { this.department = v; }
        public int getCurrentStreak() { return currentStreak; }
        public void setCurrentStreak(int v) { this.currentStreak = v; }
        public int getLongestStreak() { return longestStreak; }
        public void setLongestStreak(int v) { this.longestStreak = v; }
        public int getTotalActiveDays() { return totalActiveDays; }
        public void setTotalActiveDays(int v) { this.totalActiveDays = v; }
        public int getTotalLeetcodeSolved() { return totalLeetcodeSolved; }
        public void setTotalLeetcodeSolved(int v) { this.totalLeetcodeSolved = v; }
    }
}
