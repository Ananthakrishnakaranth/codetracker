package com.tracker.service;

import com.tracker.model.Streak;
import com.tracker.model.User;
import com.tracker.repository.StreakRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class StreakService {

    private static final Logger log = LoggerFactory.getLogger(StreakService.class);
    private final StreakRepository streakRepository;

    public StreakService(StreakRepository streakRepository) {
        this.streakRepository = streakRepository;
    }

    @Transactional
    public Streak updateStreak(User user, boolean activeToday) {
        Streak streak = streakRepository.findById(user.getId()).orElseGet(() -> {
            Streak s = new Streak();
            s.setUser(user);
            s.setCurrentStreak(0);
            s.setLongestStreak(0);
            s.setTotalActiveDays(0);
            return s;
        });

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        if (activeToday) {
            boolean continued = streak.getLastActiveDate() != null
                    && streak.getLastActiveDate().equals(yesterday);
            streak.setCurrentStreak(continued ? streak.getCurrentStreak() + 1 : 1);
            streak.setLastActiveDate(today);
            streak.setTotalActiveDays(streak.getTotalActiveDays() + 1);
            if (streak.getCurrentStreak() > streak.getLongestStreak()) {
                streak.setLongestStreak(streak.getCurrentStreak());
            }
        } else {
            if (streak.getLastActiveDate() == null || !streak.getLastActiveDate().equals(today)) {
                streak.setCurrentStreak(0);
            }
        }

        log.info("Streak for user {}: current={}, longest={}", user.getId(),
                streak.getCurrentStreak(), streak.getLongestStreak());
        return streakRepository.save(streak);
    }

    public Streak getOrCreate(User user) {
        return streakRepository.findById(user.getId()).orElseGet(() -> {
            Streak s = new Streak();
            s.setUser(user);
            s.setCurrentStreak(0);
            s.setLongestStreak(0);
            s.setTotalActiveDays(0);
            return streakRepository.save(s);
        });
    }
}
