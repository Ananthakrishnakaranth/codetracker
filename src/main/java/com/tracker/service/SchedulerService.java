package com.tracker.service;

import com.tracker.model.User;
import com.tracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);

    private final UserRepository userRepository;
    private final ProgressService progressService;

    public SchedulerService(UserRepository userRepository, ProgressService progressService) {
        this.userRepository = userRepository;
        this.progressService = progressService;
    }

    @Scheduled(cron = "${scheduler.cron}")
    public void runDailyProgressUpdate() {
        log.info("=== DAILY SCHEDULER STARTED at {} ===", LocalDateTime.now());
        List<User> allUsers = userRepository.findAll();
        int success = 0, failed = 0;
        for (User user : allUsers) {
            try { progressService.takeSnapshot(user); success++; }
            catch (Exception e) { log.error("Failed for user {}: {}", user.getId(), e.getMessage()); failed++; }
        }
        log.info("=== DONE: {} success, {} failed ===", success, failed);
    }
}
