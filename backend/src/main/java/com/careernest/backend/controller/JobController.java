package com.careernest.backend.controller;

import com.careernest.backend.dto.request.JobRequest;
import com.careernest.backend.dto.response.JobResponse;
import com.careernest.backend.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    /** Browse/search — available to any authenticated user. */
    @GetMapping
    public ResponseEntity<List<JobResponse>> search(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(jobService.search(location, keyword));
    }

    /** An employer's own postings. Declared before /{id} so "mine" isn't read as an id. */
    @GetMapping("/mine")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<List<JobResponse>> myJobs() {
        return ResponseEntity.ok(jobService.listMyJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(jobService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponse> create(@Valid @RequestBody JobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponse> update(@PathVariable String id, @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        jobService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
