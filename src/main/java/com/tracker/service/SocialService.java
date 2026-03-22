package com.tracker.service;

import com.tracker.model.*;
import com.tracker.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class SocialService {

    private static final Logger log = LoggerFactory.getLogger(SocialService.class);

    private final FollowRepository followRepository;
    private final BadgeRepository badgeRepository;
    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
    private final StreakRepository streakRepository;

    public SocialService(FollowRepository followRepository,
                         BadgeRepository badgeRepository,
                         ReactionRepository reactionRepository,
                         CommentRepository commentRepository,
                         StreakRepository streakRepository) {
        this.followRepository = followRepository;
        this.badgeRepository = badgeRepository;
        this.reactionRepository = reactionRepository;
        this.commentRepository = commentRepository;
        this.streakRepository = streakRepository;
    }

    // ── FOLLOW ──────────────────────────────────────────────

    @Transactional
    public boolean toggleFollow(User follower, User following) {
        if (follower.getId().equals(following.getId())) {
            throw new RuntimeException("Cannot follow yourself");
        }
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            Follow f = followRepository.findByFollowerAndFollowing(follower, following).get();
            followRepository.delete(f);
            return false; // unfollowed
        } else {
            Follow f = new Follow();
            f.setFollower(follower);
            f.setFollowing(following);
            followRepository.save(f);
            checkAndAwardBadge(following, Badge.BadgeType.FIRST_FOLLOWER);
            long followingCount = followRepository.countByFollower(follower);
            if (followingCount >= 10) checkAndAwardBadge(follower, Badge.BadgeType.SOCIAL_BUTTERFLY);
            return true; // followed
        }
    }

    public List<User> getFollowing(User user) {
        return followRepository.findFollowingUsers(user);
    }

    public List<User> getFollowers(User user) {
        return followRepository.findFollowerUsers(user);
    }

    public boolean isFollowing(User follower, User following) {
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }

    // ── BADGES ──────────────────────────────────────────────

    @Transactional
    public void checkAndAwardBadge(User user, Badge.BadgeType type) {
        if (!badgeRepository.existsByUserAndType(user, type)) {
            Badge badge = new Badge();
            badge.setUser(user);
            badge.setType(type);
            badgeRepository.save(badge);
            log.info("Badge awarded to user {}: {}", user.getId(), type.getTitle());
        }
    }

    @Transactional
    public void checkAllBadges(User user, int totalSolved, int currentStreak) {
        if (totalSolved >= 1)   checkAndAwardBadge(user, Badge.BadgeType.FIRST_SOLVE);
        if (totalSolved >= 50)  checkAndAwardBadge(user, Badge.BadgeType.PROBLEMS_50);
        if (totalSolved >= 100) checkAndAwardBadge(user, Badge.BadgeType.PROBLEMS_100);
        if (totalSolved >= 500) checkAndAwardBadge(user, Badge.BadgeType.PROBLEMS_500);
        if (currentStreak >= 7)   checkAndAwardBadge(user, Badge.BadgeType.STREAK_7);
        if (currentStreak >= 30)  checkAndAwardBadge(user, Badge.BadgeType.STREAK_30);
        if (currentStreak >= 100) checkAndAwardBadge(user, Badge.BadgeType.STREAK_100);
    }

    public List<Badge> getUserBadges(User user) {
        return badgeRepository.findByUser(user);
    }

    // ── REACTIONS ────────────────────────────────────────────

    @Transactional
    public void addReaction(User user, User targetUser, Reaction.ReactionType type) {
        String today = LocalDate.now().toString();
        if (reactionRepository.existsByUserAndTargetUserAndReactionDate(user, targetUser, today)) {
            throw new RuntimeException("Already reacted today!");
        }
        Reaction reaction = new Reaction();
        reaction.setUser(user);
        reaction.setTargetUser(targetUser);
        reaction.setType(type);
        reaction.setReactionDate(today);
        reactionRepository.save(reaction);
    }

    public Map<String, Long> getReactionCounts(User targetUser, String date) {
        List<Reaction> reactions = reactionRepository.findByTargetUserAndDate(targetUser, date);
        Map<String, Long> counts = new HashMap<>();
        for (Reaction.ReactionType type : Reaction.ReactionType.values()) {
            counts.put(type.name(), reactions.stream()
                    .filter(r -> r.getType() == type).count());
        }
        return counts;
    }

    // ── COMMENTS ─────────────────────────────────────────────

    @Transactional
    public Comment addComment(User author, User targetUser, String content) {
        if (content == null || content.isBlank()) throw new RuntimeException("Comment cannot be empty");
        if (content.length() > 200) throw new RuntimeException("Comment too long (max 200 chars)");
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setTargetUser(targetUser);
        comment.setContent(content);
        comment.setAchievementDate(LocalDate.now().toString());
        return commentRepository.save(comment);
    }

    public List<Comment> getUserComments(User targetUser) {
        return commentRepository.findByTargetUserOrderByCreatedAtDesc(targetUser);
    }

    // ── FRIENDS LEADERBOARD ──────────────────────────────────

    public List<Streak> getFriendsLeaderboard(User user) {
        List<User> following = followRepository.findFollowingUsers(user);
        following.add(user); // include self
        return streakRepository.findAllOrderByCurrentStreakDesc()
                .stream()
                .filter(s -> following.stream().anyMatch(f -> f.getId().equals(s.getUser().getId())))
                .toList();
    }
}
