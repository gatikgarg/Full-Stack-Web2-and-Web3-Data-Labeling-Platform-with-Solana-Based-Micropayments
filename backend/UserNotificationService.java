package com.rwtool.service;

import com.rwtool.model.UserNotification;
import com.rwtool.repository.UserNotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserNotificationService {

    private final UserNotificationRepository repo;

    public UserNotificationService(UserNotificationRepository repo) {
        this.repo = repo;
    }

    // List notifications for a user (sorted by newest first)
    public List<UserNotification> listForUser(String userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<UserNotification> get(String id) {
        return repo.findById(id);
    }

    // Create or update a notification
    public UserNotification save(UserNotification n) {
        return repo.save(n);
    }

    // Mark a single notification as read
    public void markRead(String id) {
        repo.findById(id).ifPresent(n -> {
            n.setRead(true);
            repo.save(n);
        });
    }

    // Mark all notifications as read for a user
    @Transactional
    public void markAllRead(String userId) {
        List<UserNotification> list = repo.findByUserIdOrderByCreatedAtDesc(userId);
        for (UserNotification n : list) {
            if (!n.isRead()) {
                n.setRead(true);
            }
        }
        repo.saveAll(list);
    }

    // Delete a single notification
    public void delete(String id) {
        repo.deleteById(id);
    }

    // Clear all notifications for a user, returns number deleted
    public long clearAll(String userId) {
        return repo.deleteByUserId(userId);
    }
}
