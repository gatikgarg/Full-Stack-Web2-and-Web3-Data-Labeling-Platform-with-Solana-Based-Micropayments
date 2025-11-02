package com.rwtool.repository;

import com.rwtool.model.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, String> {
    List<UserNotification> findByUserIdOrderByCreatedAtDesc(String userId);
    long deleteByUserId(String userId);
}
