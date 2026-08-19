package com.careernest.backend.controller;

import com.careernest.backend.dto.request.ApplicationStatusRequest;
import com.careernest.backend.dto.response.ApplicationResponse;
import com.careernest.backend.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping("/{jobId}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApplicationResponse> apply(@PathVariable String jobId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobApplicationService.apply(jobId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<List<ApplicationResponse>> myApplications() {
        return ResponseEntity.ok(jobApplicationService.listMyApplications());
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<List<ApplicationResponse>> applicationsForJob(@PathVariable String jobId) {
        return ResponseEntity.ok(jobApplicationService.listApplicationsForJob(jobId));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable String id, @Valid @RequestBody ApplicationStatusRequest request) {
        return ResponseEntity.ok(jobApplicationService.updateStatus(id, request.getStatus()));
    }
}
