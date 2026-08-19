package com.careernest.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Public landing endpoint. Without this, hitting the API root in a browser returns a bare
 * 401 (no handler + everything authenticated), which reads like an outage when it isn't.
 */
@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> index() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("service", "CareerNest API");
        body.put("status", "running");
        body.put("documentation", "All endpoints below are prefixed with this host.");
        body.put("health", "/actuator/health");

        Map<String, Object> endpoints = new LinkedHashMap<>();
        endpoints.put("public", Map.of(
                "POST /api/auth/register", "Create a JOB_SEEKER or EMPLOYER account",
                "POST /api/auth/login", "Exchange credentials for a JWT"));
        endpoints.put("authenticated", Map.of(
                "GET /api/jobs", "Search jobs (optional ?keyword= and ?location=)",
                "GET /api/jobs/{id}", "Job details"));
        endpoints.put("employer", Map.of(
                "POST /api/jobs", "Create a posting",
                "PUT /api/jobs/{id}", "Update your posting",
                "DELETE /api/jobs/{id}", "Remove your posting",
                "GET /api/jobs/mine", "Your postings",
                "GET /api/applications/job/{jobId}", "Applicants for your posting",
                "PATCH /api/applications/{id}/status", "Update an applicant's status"));
        endpoints.put("jobSeeker", Map.of(
                "POST /api/applications/{jobId}", "Apply to a job",
                "GET /api/applications/my", "Your applications"));

        body.put("endpoints", endpoints);
        body.put("note", "Protected routes require an 'Authorization: Bearer <token>' header.");

        return ResponseEntity.ok(body);
    }
}
