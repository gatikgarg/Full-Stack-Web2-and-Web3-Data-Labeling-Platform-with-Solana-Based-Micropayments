package com.example.rwtool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subscriptions")
@CrossOrigin(origins = "http://localhost:3000")
public class SubscriptionController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Fetch all subscription records
    @GetMapping
    public List<Map<String, Object>> getAllSubscriptions() {
        String sql = "SELECT * FROM SubscriptionStatus";
        return jdbcTemplate.queryForList(sql);
    }

    // Fetch subscriptions filtered by status (case-insensitive)
    @GetMapping("/status/{status}")
    public List<Map<String, Object>> getSubscriptionsByStatus(@PathVariable String status) {
        String sql = "SELECT * FROM SubscriptionStatus WHERE LOWER(status) = LOWER(?)";
        return jdbcTemplate.queryForList(sql, status);
    }
}
