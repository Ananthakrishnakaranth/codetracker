package com.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reactions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "target_user_id", "reaction_date"}))
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // who reacted

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser; // whose streak got reacted to

    @Enumerated(EnumType.STRING)
    private ReactionType type;

    private String reactionDate; // YYYY-MM-DD of the streak day

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    public enum ReactionType {
        FIRE("🔥"), CLAP("👏"), ROCKET("🚀"), HEART("❤️"), TROPHY("🏆");

        private final String emoji;
        ReactionType(String emoji) { this.emoji = emoji; }
        public String getEmoji() { return emoji; }
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public User getTargetUser() { return targetUser; }
    public void setTargetUser(User targetUser) { this.targetUser = targetUser; }
    public ReactionType getType() { return type; }
    public void setType(ReactionType type) { this.type = type; }
    public String getReactionDate() { return reactionDate; }
    public void setReactionDate(String reactionDate) { this.reactionDate = reactionDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
