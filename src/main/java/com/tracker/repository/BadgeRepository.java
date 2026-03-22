package com.tracker.repository;

import com.tracker.model.Badge;
import com.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    List<Badge> findByUser(User user);
    boolean existsByUserAndType(User user, Badge.BadgeType type);
}
