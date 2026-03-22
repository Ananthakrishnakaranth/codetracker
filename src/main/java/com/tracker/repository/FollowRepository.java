package com.tracker.repository;

import com.tracker.model.Follow;
import com.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    boolean existsByFollowerAndFollowing(User follower, User following);
    List<Follow> findByFollower(User follower);
    List<Follow> findByFollowing(User following);
    long countByFollower(User follower);
    long countByFollowing(User following);

    @Query("SELECT f.following FROM Follow f WHERE f.follower = :user")
    List<User> findFollowingUsers(User user);

    @Query("SELECT f.follower FROM Follow f WHERE f.following = :user")
    List<User> findFollowerUsers(User user);
}
