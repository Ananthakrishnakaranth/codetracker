package com.tracker.repository;

import com.tracker.model.Reaction;
import com.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    List<Reaction> findByTargetUser(User targetUser);
    boolean existsByUserAndTargetUserAndReactionDate(User user, User targetUser, String date);

    @Query("SELECT r FROM Reaction r WHERE r.targetUser = :user AND r.reactionDate = :date")
    List<Reaction> findByTargetUserAndDate(User user, String date);
}
