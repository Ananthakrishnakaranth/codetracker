package com.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "progress_snapshots",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"}))
public class ProgressSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate date;
    private int leetcodeTotalSolved;
    private int leetcodeEasySolved;
    private int leetcodeMediumSolved;
    private int leetcodeHardSolved;
    private int leetcodeNewSolvedToday;
    private int githubCommitsToday;
    private int githubTotalCommitsThisYear;
    private boolean activeDay;

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public int getLeetcodeTotalSolved() { return leetcodeTotalSolved; }
    public void setLeetcodeTotalSolved(int v) { this.leetcodeTotalSolved = v; }
    public int getLeetcodeEasySolved() { return leetcodeEasySolved; }
    public void setLeetcodeEasySolved(int v) { this.leetcodeEasySolved = v; }
    public int getLeetcodeMediumSolved() { return leetcodeMediumSolved; }
    public void setLeetcodeMediumSolved(int v) { this.leetcodeMediumSolved = v; }
    public int getLeetcodeHardSolved() { return leetcodeHardSolved; }
    public void setLeetcodeHardSolved(int v) { this.leetcodeHardSolved = v; }
    public int getLeetcodeNewSolvedToday() { return leetcodeNewSolvedToday; }
    public void setLeetcodeNewSolvedToday(int v) { this.leetcodeNewSolvedToday = v; }
    public int getGithubCommitsToday() { return githubCommitsToday; }
    public void setGithubCommitsToday(int v) { this.githubCommitsToday = v; }
    public int getGithubTotalCommitsThisYear() { return githubTotalCommitsThisYear; }
    public void setGithubTotalCommitsThisYear(int v) { this.githubTotalCommitsThisYear = v; }
    public boolean isActiveDay() { return activeDay; }
    public void setActiveDay(boolean activeDay) { this.activeDay = activeDay; }
}
