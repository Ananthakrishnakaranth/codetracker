package com.tracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class GitHubService {

    private static final Logger log = LoggerFactory.getLogger(GitHubService.class);

    @Value("${github.api.base-url}")
    private String baseUrl;

    @Value("${github.api.token}")
    private String githubToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public int fetchTodayCommitCount(String username) {
        if (username == null || username.isBlank()) return 0;
        try {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE);
            String url = baseUrl + "/search/commits?q=author:" + username
                    + "+author-date:" + today + ".." + tomorrow + "&per_page=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/vnd.github.cloak-preview+json");
            if (githubToken != null && !githubToken.isBlank() && !githubToken.startsWith("ghp_your")) {
                headers.set("Authorization", "token " + githubToken);
            }

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            int count = root.path("total_count").asInt(0);
            log.info("GitHub commits today for {}: {}", username, count);
            return count;
        } catch (Exception e) {
            log.error("Failed to fetch GitHub commits for {}: {}", username, e.getMessage());
            return 0;
        }
    }
}
