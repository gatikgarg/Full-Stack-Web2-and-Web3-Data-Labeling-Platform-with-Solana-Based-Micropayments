package com.rwtool.controller;

import com.rwtool.model.SubscriptionRequest;
import com.rwtool.model.User;
import com.rwtool.model.UserNotification;
import com.rwtool.model.User.Role;
import com.rwtool.repository.SubscriptionRequestRepository;
import com.rwtool.repository.UserRepository;
import com.rwtool.service.UserNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/file-events")
@CrossOrigin(origins = "http://localhost:3000")
public class FileEventController {

    private final UserNotificationService userNotificationService;
    private final UserRepository userRepository;
    private final SubscriptionRequestRepository subscriptionRequestRepository;

    public FileEventController(UserNotificationService userNotificationService,
                               UserRepository userRepository,
                               SubscriptionRequestRepository subscriptionRequestRepository) {
        this.userNotificationService = userNotificationService;
        this.userRepository = userRepository;
        this.subscriptionRequestRepository = subscriptionRequestRepository;
    }

    @PostMapping("/new")
    public ResponseEntity<?> newFile(@RequestBody Map<String, String> body) {
        String folder = body.get("folder");
        String fileName = body.get("fileName");
        if (folder == null || fileName == null) {
            return ResponseEntity.badRequest().body("folder and fileName are required");
        }

        String title = "New File Added";
        String message = fileName + " added to " + folder;

        // notify admins
        List<User> admins = userRepository.findAllByRole(Role.ADMIN);
        for (User admin : admins) {
            UserNotification n = new UserNotification();
            n.setUserId(admin.getEmail());
            n.setType("file");
            n.setTitle(title);
            n.setMessage(message);
            n.setRead(false);
            userNotificationService.save(n);
        }

        // notify subscribers with APPROVED status for this domain
        List<SubscriptionRequest> approved = subscriptionRequestRepository.findByDomainNameAndStatus(folder, "APPROVED");
        for (SubscriptionRequest sr : approved) {
            UserNotification n = new UserNotification();
            n.setUserId(sr.getUserEmail());
            n.setType("file");
            n.setTitle(title);
            n.setMessage(message);
            n.setRead(false);
            userNotificationService.save(n);
        }

        return ResponseEntity.ok(Map.of("status", "ok", "notifiedAdmins", admins.size(), "notifiedUsers", approved.size()));
    }
}
