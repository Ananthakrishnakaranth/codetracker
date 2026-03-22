package com.tracker.controller;

import com.tracker.model.*;
import com.tracker.service.SocialService;
import com.tracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/social")
@CrossOrigin(origins = "*")
public class SocialController {

    private final SocialService socialService;
    private final UserService userService;

    public SocialController(SocialService socialService, UserService userService) {
        this.socialService = socialService;
        this.userService = userService;
    }

    // ── FOLLOW ──────────────────────────────────────────────

    @PostMapping("/follow/{targetUserId}")
    public ResponseEntity<Map<String, Object>> toggleFollow(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long targetUserId) {
        User me = userService.getUserByEmail(userDetails.getUsername());
        User target = userService.getUserById(targetUserId);
        boolean isNowFollowing = socialService.toggleFollow(me, target);
        Map<String, Object> resp = new HashMap<>();
        resp.put("following", isNowFollowing);
        resp.put("message", isNowFollowing ? "Now following " + target.getName() : "Unfollowed " + target.getName());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/following")
    public ResponseEntity<List<Map<String, Object>>> getFollowing(
            @AuthenticationPrincipal UserDetails userDetails) {
        User me = userService.getUserByEmail(userDetails.getUsername());
        List<User> following = socialService.getFollowing(me);
        return ResponseEntity.ok(following.stream().map(this::userToMap).toList());
    }

    @GetMapping("/followers")
    public ResponseEntity<List<Map<String, Object>>> getFollowers(
            @AuthenticationPrincipal UserDetails userDetails) {
        User me = userService.getUserByEmail(userDetails.getUsername());
        List<User> followers = socialService.getFollowers(me);
        return ResponseEntity.ok(followers.stream().map(this::userToMap).toList());
    }

    @GetMapping("/is-following/{targetUserId}")
    public ResponseEntity<Map<String, Boolean>> isFollowing(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long targetUserId) {
        User me = userService.getUserByEmail(userDetails.getUsername());
        User target = userService.getUserById(targetUserId);
        return ResponseEntity.ok(Map.of("following", socialService.isFollowing(me, target)));
    }

    // ── BADGES ──────────────────────────────────────────────

    @GetMapping("/badges")
    public ResponseEntity<List<Map<String, Object>>> getMyBadges(
            @AuthenticationPrincipal UserDetails userDetails) {
        User me = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(badgesToMaps(socialService.getUserBadges(me)));
    }

    @GetMapping("/badges/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserBadges(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(badgesToMaps(socialService.getUserBadges(user)));
    }

    // ── REACTIONS ────────────────────────────────────────────

    @PostMapping("/react/{targetUserId}")
    public ResponseEntity<Map<String, String>> addReaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long targetUserId,
            @RequestBody Map<String, String> body) {
        User me = userService.getUserByEmail(userDetails.getUsername());
        User target = userService.getUserById(targetUserId);
        Reaction.ReactionType type = Reaction.ReactionType.valueOf(body.get("type"));
        socialService.addReaction(me, target, type);
        return ResponseEntity.ok(Map.of("message", "Reaction added!"));
    }

    @GetMapping("/reactions/{targetUserId}")
    public ResponseEntity<Map<String, Long>> getReactions(
            @PathVariable Long targetUserId,
            @RequestParam(defaultValue = "") String date) {
        User target = userService.getUserById(targetUserId);
        String d = date.isBlank() ? java.time.LocalDate.now().toString() : date;
        return ResponseEntity.ok(socialService.getReactionCounts(target, d));
    }

    // ── COMMENTS ─────────────────────────────────────────────

    @PostMapping("/comment/{targetUserId}")
    public ResponseEntity<Map<String, Object>> addComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long targetUserId,
            @RequestBody Map<String, String> body) {
        User me = userService.getUserByEmail(userDetails.getUsername());
        User target = userService.getUserById(targetUserId);
        Comment comment = socialService.addComment(me, target, body.get("content"));
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", comment.getId());
        resp.put("content", comment.getContent());
        resp.put("authorName", me.getName());
        resp.put("createdAt", comment.getCreatedAt().toString());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/comments/{targetUserId}")
    public ResponseEntity<List<Map<String, Object>>> getComments(@PathVariable Long targetUserId) {
        User target = userService.getUserById(targetUserId);
        List<Comment> comments = socialService.getUserComments(target);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Comment c : comments) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("content", c.getContent());
            map.put("authorName", c.getAuthor().getName());
            map.put("authorId", c.getAuthor().getId());
            map.put("createdAt", c.getCreatedAt().toString());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    // ── FRIENDS LEADERBOARD ──────────────────────────────────

    @GetMapping("/leaderboard/friends")
    public ResponseEntity<List<Map<String, Object>>> getFriendsLeaderboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        User me = userService.getUserByEmail(userDetails.getUsername());
        List<Streak> streaks = socialService.getFriendsLeaderboard(me);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Streak s : streaks) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", s.getUser().getId());
            map.put("name", s.getUser().getName());
            map.put("department", s.getUser().getDepartment());
            map.put("currentStreak", s.getCurrentStreak());
            map.put("longestStreak", s.getLongestStreak());
            map.put("totalActiveDays", s.getTotalActiveDays());
            map.put("isMe", s.getUser().getId().equals(me.getId()));
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    // ── SEARCH USERS ─────────────────────────────────────────

    @GetMapping("/users/search")
    public ResponseEntity<List<Map<String, Object>>> searchUsers(
            @RequestParam String q,
            @AuthenticationPrincipal UserDetails userDetails) {
        User me = userService.getUserByEmail(userDetails.getUsername());
        List<User> users = userService.searchUsers(q);
        return ResponseEntity.ok(users.stream()
                .filter(u -> !u.getId().equals(me.getId()))
                .map(u -> {
                    Map<String, Object> map = userToMap(u);
                    map.put("isFollowing", socialService.isFollowing(me, u));
                    return map;
                }).toList());
    }

    // ── HELPERS ──────────────────────────────────────────────

    private Map<String, Object> userToMap(User u) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", u.getId());
        map.put("name", u.getName());
        map.put("department", u.getDepartment() != null ? u.getDepartment() : "");
        map.put("leetcodeUsername", u.getLeetcodeUsername() != null ? u.getLeetcodeUsername() : "");
        map.put("githubUsername", u.getGithubUsername() != null ? u.getGithubUsername() : "");
        return map;
    }

    private List<Map<String, Object>> badgesToMaps(List<Badge> badges) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Badge b : badges) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", b.getType().name());
            map.put("title", b.getType().getTitle());
            map.put("description", b.getType().getDescription());
            map.put("emoji", b.getType().getEmoji());
            map.put("earnedAt", b.getEarnedAt().toString());
            result.add(map);
        }
        return result;
    }
}
