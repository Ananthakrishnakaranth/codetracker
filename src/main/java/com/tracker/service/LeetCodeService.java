package com.tracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LeetCodeService {

    private static final Logger log = LoggerFactory.getLogger(LeetCodeService.class);

    @Value("${leetcode.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LeetCodeStats fetchStats(String username) {
        if (username == null || username.isBlank()) return LeetCodeStats.empty();
        try {
            String url = baseUrl + "/" + username;
            log.info("Fetching LeetCode stats for: {}", username);
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            if (root.has("status") && "error".equals(root.get("status").asText())) {
                log.warn("LeetCode user not found: {}", username);
                return LeetCodeStats.empty();
            }
            LeetCodeStats stats = new LeetCodeStats(
                root.path("totalSolved").asInt(0),
                root.path("easySolved").asInt(0),
                root.path("mediumSolved").asInt(0),
                root.path("hardSolved").asInt(0)
            );
            log.info("LeetCode stats for {}: total={}", username, stats.getTotalSolved());
            return stats;
        } catch (Exception e) {
            log.error("Failed to fetch LeetCode stats for {}: {}", username, e.getMessage());
            return LeetCodeStats.empty();
        }
    }

    public static class LeetCodeStats {
        private final int totalSolved;
        private final int easySolved;
        private final int mediumSolved;
        private final int hardSolved;

        public LeetCodeStats(int totalSolved, int easySolved, int mediumSolved, int hardSolved) {
            this.totalSolved = totalSolved;
            this.easySolved = easySolved;
            this.mediumSolved = mediumSolved;
            this.hardSolved = hardSolved;
        }

        public static LeetCodeStats empty() { return new LeetCodeStats(0, 0, 0, 0); }
        public int getTotalSolved() { return totalSolved; }
        public int getEasySolved() { return easySolved; }
        public int getMediumSolved() { return mediumSolved; }
        public int getHardSolved() { return hardSolved; }
    }
}
