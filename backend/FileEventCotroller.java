package com.rwtool.controller;

import com.rwtool.model.UserNotification;
import com.rwtool.service.UserNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    private final UserNotificationService service;

    public NotificationController(UserNotificationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UserNotification>> list(@RequestParam String userId) {
        return ResponseEntity.ok(service.listForUser(userId));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markRead(@PathVariable String id) {
        service.markRead(id);
        Map<String, Object> res = new HashMap<>();
        res.put("status", "ok");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllRead(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        service.markAllRead(userId);
        Map<String, Object> res = new HashMap<>();
        res.put("status", "ok");
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        service.delete(id);
        Map<String, Object> res = new HashMap<>();
        res.put("status", "ok");
        return ResponseEntity.ok(res);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clearAll(@RequestParam String userId) {
        long n = service.clearAll(userId);
        Map<String, Object> res = new HashMap<>();
        res.put("deleted", n);
        return ResponseEntity.ok(res);
    }

    // Optional: endpoint to seed a notification for testing
    @PostMapping
    public ResponseEntity<UserNotification> create(@RequestBody Map<String, String> body) {
        UserNotification n = new UserNotification();
        n.setUserId(body.get("userId"));
        n.setType(body.getOrDefault("type", "file"));
        n.setTitle(body.getOrDefault("title", "Notification"));
        n.setMessage(body.getOrDefault("message", ""));
        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);
        return ResponseEntity.ok(service.save(n));
    }
}
