package com.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "badges")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private BadgeType type;

    private LocalDateTime earnedAt;

    @PrePersist
    public void prePersist() { this.earnedAt = LocalDateTime.now(); }

    public enum BadgeType {
        FIRST_SOLVE("First Solve", "Solved your first LeetCode problem!", "⚡"),
        STREAK_7("Week Warrior", "Maintained a 7-day streak!", "🔥"),
        STREAK_30("Month Master", "Maintained a 30-day streak!", "🏆"),
        STREAK_100("Century Club", "Maintained a 100-day streak!", "💯"),
        PROBLEMS_50("Problem Solver", "Solved 50 LeetCode problems!", "🧩"),
        PROBLEMS_100("Century Coder", "Solved 100 LeetCode problems!", "💪"),
        PROBLEMS_500("Elite Coder", "Solved 500 LeetCode problems!", "👑"),
        COMMIT_STREAK("Git Master", "Made commits 7 days in a row!", "⬡"),
        SOCIAL_BUTTERFLY("Social Butterfly", "Following 10+ coders!", "🦋"),
        FIRST_FOLLOWER("Rising Star", "Got your first follower!", "⭐");

        private final String title;
        private final String description;
        private final String emoji;

        BadgeType(String title, String description, String emoji) {
            this.title = title;
            this.description = description;
            this.emoji = emoji;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getEmoji() { return emoji; }
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public BadgeType getType() { return type; }
    public void setType(BadgeType type) { this.type = type; }
    public LocalDateTime getEarnedAt() { return earnedAt; }
}
