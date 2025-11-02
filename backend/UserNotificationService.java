package com.rwtool.service;

import com.rwtool.dto.ApprovalDecisionDTO;
import com.rwtool.dto.SubscriptionRequestDTO;
import com.rwtool.model.SubscriptionRequest;
import com.rwtool.model.User;
import com.rwtool.repository.SubscriptionRequestRepository;
import com.rwtool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// ADDED (notifications)
import com.rwtool.model.UserNotification;
import com.rwtool.model.User.Role;
import com.rwtool.service.UserNotificationService;

@Service
public class SubscriptionRequestService {

    @Autowired
    private SubscriptionRequestRepository subscriptionRequestRepository;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private UserRepository userRepository;

    // ADDED (notifications)
    @Autowired
    private UserNotificationService userNotificationService;

    public List<SubscriptionRequest> getAllRequests() {
        return subscriptionRequestRepository.findAll();
    }

    public List<SubscriptionRequest> getPendingRequests() {
        return subscriptionRequestRepository.findByStatus("PENDING");
    }

    public List<SubscriptionRequest> getRequestsByUser(String userEmail) {
        return subscriptionRequestRepository.findByUserEmailOrderByRequestedDateDesc(userEmail);
    }

    public SubscriptionRequest getRequestById(String id) {
        return subscriptionRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription request not found with id: " + id));
    }

    @Transactional
    public SubscriptionRequest createRequest(SubscriptionRequestDTO requestDTO) {
        // Resolve current user's email from JWT/SecurityContext (fallback to DTO)
        String currentEmail = null;
        Authentication auth = SecurityContextHolder.getContext() != null
                ? SecurityContextHolder.getContext().getAuthentication()
                : null;
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails ud) {
                currentEmail = ud.getUsername();
            } else if (principal instanceof String s) {
                currentEmail = "anonymousUser".equalsIgnoreCase(s) ? null : s;
            }
        }
        if (currentEmail == null || currentEmail.isBlank()) {
            currentEmail = requestDTO.getUserEmail();
        }

        // Ensure no duplicate pending/approved
        Optional<SubscriptionRequest> existingRequest = subscriptionRequestRepository
                .findByUserEmailAndDomainIdAndStatus(currentEmail, requestDTO.getDomainId(), "PENDING");
        if (existingRequest.isPresent()) {
            throw new RuntimeException("You already have a pending request for this domain");
        }

        Optional<SubscriptionRequest> approvedRequest = subscriptionRequestRepository
                .findByUserEmailAndDomainIdAndStatus(currentEmail, requestDTO.getDomainId(), "APPROVED");
        if (approvedRequest.isPresent()) {
            throw new RuntimeException("You already have approved access to this domain");
        }

        // Build request from authoritative user record
        final String emailLookup = currentEmail;
        User user = userRepository.findByEmail(emailLookup)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + emailLookup));

        SubscriptionRequest request = new SubscriptionRequest();
        request.setId(UUID.randomUUID().toString());
        request.setDomainId(requestDTO.getDomainId());
        request.setDomainName(requestDTO.getDomainName());
        request.setRequestReason(requestDTO.getRequestReason());
        request.setUserName(user.getFullName());
        request.setUserEmail(user.getEmail());
        request.setUserDepartment(user.getDomain());
        request.setUserRole(user.getRole() != null ? user.getRole().name() : null);
        request.setStatus("PENDING");
        request.setRequestedDate(LocalDateTime.now());

        // Save and notify admins of new subscription request
        SubscriptionRequest saved = subscriptionRequestRepository.save(request);

        List<User> admins = userRepository.findAllByRole(Role.ADMIN);
        for (User admin : admins) {
            UserNotification n = new UserNotification();
            n.setUserId(admin.getEmail());
            n.setType("subscription");
            n.setTitle("New Subscription Request");
            n.setMessage(saved.getUserEmail() + " requested access to " + saved.getDomainName());
            n.setRead(false);
            userNotificationService.save(n);
        }

        return saved;
    }

    @Transactional
    public SubscriptionRequest approveRequest(String requestId) {
        SubscriptionRequest request = getRequestById(requestId);

        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("Only pending requests can be approved");
        }

        request.setStatus("APPROVED");
        request.setReviewedDate(LocalDateTime.now());
        request.setRejectionReason(null);

        // Best effort to add to group
        try {
            userGroupService.addUserToGroupByDomain(request.getUserEmail(), request.getDomainName());
        } catch (Exception e) {
            System.err.println("Failed to add user to group: " + e.getMessage());
        }

        // Save and notify requesting user (approved)
        SubscriptionRequest saved = subscriptionRequestRepository.save(request);

        UserNotification n = new UserNotification();
        n.setUserId(saved.getUserEmail());
        n.setType("accepted");
        n.setTitle("Request Approved");
        n.setMessage("Access granted to " + saved.getDomainName());
        n.setRead(false);
        userNotificationService.save(n);

        return saved;
    }

    @Transactional
    public SubscriptionRequest rejectRequest(String requestId, String rejectionReason) {
        SubscriptionRequest request = getRequestById(requestId);

        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("Only pending requests can be rejected");
        }

        request.setStatus("REJECTED");
        request.setReviewedDate(LocalDateTime.now());
        request.setRejectionReason(rejectionReason);

        // Save and notify requesting user (rejected)
        SubscriptionRequest saved = subscriptionRequestRepository.save(request);

        UserNotification n = new UserNotification();
        n.setUserId(saved.getUserEmail());
        n.setType("rejected");
        n.setTitle("Request Rejected");
        n.setMessage(rejectionReason != null ? rejectionReason : "Request rejected");
        n.setRead(false);
        userNotificationService.save(n);

        return saved;
    }

    @Transactional
    public SubscriptionRequest processRequest(String requestId, ApprovalDecisionDTO decision) {
        if ("APPROVE".equalsIgnoreCase(decision.getAction())) {
            return approveRequest(requestId);
        } else if ("REJECT".equalsIgnoreCase(decision.getAction())) {
            String reason = decision.getRejectionReason();
            if (reason == null || reason.trim().isEmpty()) {
                reason = "Request rejected by admin";
            }
            return rejectRequest(requestId, reason);
        } else {
            throw new RuntimeException("Invalid action. Must be APPROVE or REJECT");
        }
    }

    @Transactional
    public void cancelRequest(String requestId, String userEmail) {
        SubscriptionRequest request = getRequestById(requestId);

        if (!request.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("You can only cancel your own requests");
        }

        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("Only pending requests can be cancelled");
        }

        subscriptionRequestRepository.delete(request);
    }
}
