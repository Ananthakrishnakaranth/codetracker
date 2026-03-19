package com.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "streaks")
public class Streak {

    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private int currentStreak;
    private int longestStreak;
    private LocalDate lastActiveDate;
    private int totalActiveDays;

    public Long getUserId() { return userId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int v) { this.currentStreak = v; }
    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int v) { this.longestStreak = v; }
    public LocalDate getLastActiveDate() { return lastActiveDate; }
    public void setLastActiveDate(LocalDate v) { this.lastActiveDate = v; }
    public int getTotalActiveDays() { return totalActiveDays; }
    public void setTotalActiveDays(int v) { this.totalActiveDays = v; }
}
