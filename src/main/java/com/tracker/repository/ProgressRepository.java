package com.tracker.repository;

import com.tracker.model.ProgressSnapshot;
import com.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<ProgressSnapshot, Long> {
    Optional<ProgressSnapshot> findByUserAndDate(User user, LocalDate date);
    List<ProgressSnapshot> findByUserOrderByDateDesc(User user);

    @Query("SELECT p FROM ProgressSnapshot p WHERE p.user = :user AND p.date >= :from ORDER BY p.date DESC")
    List<ProgressSnapshot> findRecentByUser(User user, LocalDate from);

    Optional<ProgressSnapshot> findTopByUserOrderByDateDesc(User user);
}
