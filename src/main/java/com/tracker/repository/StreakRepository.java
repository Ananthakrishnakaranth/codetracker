package com.tracker.repository;

import com.tracker.model.Streak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StreakRepository extends JpaRepository<Streak, Long> {
    @Query("SELECT s FROM Streak s ORDER BY s.currentStreak DESC")
    List<Streak> findAllOrderByCurrentStreakDesc();
}
