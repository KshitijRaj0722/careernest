package com.careernest.backend.service.impl;

import com.careernest.backend.dto.response.ApplicationResponse;
import com.careernest.backend.exception.DuplicateApplicationException;
import com.careernest.backend.exception.ForbiddenOperationException;
import com.careernest.backend.exception.ResourceNotFoundException;
import com.careernest.backend.model.Job;
import com.careernest.backend.model.JobApplication;
import com.careernest.backend.model.JobApplication.ApplicationStatus;
import com.careernest.backend.model.User;
import com.careernest.backend.repository.JobApplicationRepository;
import com.careernest.backend.repository.JobRepository;
import com.careernest.backend.repository.UserRepository;
import com.careernest.backend.security.CurrentUserProvider;
import com.careernest.backend.service.JobApplicationService;
import com.careernest.backend.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    private final SmsService smsService;

    @Override
    public ApplicationResponse apply(String jobId) {
        User seeker = currentUserProvider.requireCurrentUser();
        Job job = requireJob(jobId);

        if (jobApplicationRepository.existsByJobIdAndJobSeekerId(jobId, seeker.getId())) {
            throw new DuplicateApplicationException("You have already applied to this job");
        }

        JobApplication application = jobApplicationRepository.save(JobApplication.builder()
                .jobId(jobId)
                .jobSeekerId(seeker.getId())
                .status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .build());

        smsService.sendSms(seeker.getPhoneNumber(),
                "CareerNest: your application for '" + job.getTitle() + "' has been submitted.");

        return ApplicationResponse.from(application, job.getTitle(), seeker);
    }

    @Override
    public List<ApplicationResponse> listMyApplications() {
        User seeker = currentUserProvider.requireCurrentUser();
        List<JobApplication> applications = jobApplicationRepository.findByJobSeekerId(seeker.getId());

        Map<String, Job> jobsById = loadJobsFor(applications);

        return applications.stream()
                .map(a -> ApplicationResponse.from(a, titleOf(jobsById, a.getJobId()), seeker))
                .toList();
    }

    @Override
    public List<ApplicationResponse> listApplicationsForJob(String jobId) {
        Job job = requireOwnedJob(jobId);
        List<JobApplication> applications = jobApplicationRepository.findByJobId(jobId);

        Map<String, User> seekersById = loadSeekersFor(applications);

        return applications.stream()
                .map(a -> ApplicationResponse.from(a, job.getTitle(), seekersById.get(a.getJobSeekerId())))
                .toList();
    }

    @Override
    public ApplicationResponse updateStatus(String applicationId, ApplicationStatus status) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        Job job = requireOwnedJob(application.getJobId());

        application.setStatus(status);
        JobApplication saved = jobApplicationRepository.save(application);

        User seeker = userRepository.findById(application.getJobSeekerId()).orElse(null);
        if (seeker != null) {
            smsService.sendSms(seeker.getPhoneNumber(),
                    "CareerNest: your application for '" + job.getTitle() + "' is now " + status + ".");
        }

        return ApplicationResponse.from(saved, job.getTitle(), seeker);
    }

    private Job requireJob(String jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
    }

    private Job requireOwnedJob(String jobId) {
        Job job = requireJob(jobId);
        User employer = currentUserProvider.requireCurrentUser();
        if (!employer.getId().equals(job.getEmployerId())) {
            throw new ForbiddenOperationException("You can only review applications for your own job postings");
        }
        return job;
    }

    /** Batch-loads referenced jobs so the list endpoints don't issue a query per row. */
    private Map<String, Job> loadJobsFor(List<JobApplication> applications) {
        List<String> jobIds = applications.stream().map(JobApplication::getJobId).distinct().toList();
        return jobRepository.findAllById(jobIds).stream()
                .collect(Collectors.toMap(Job::getId, Function.identity()));
    }

    private Map<String, User> loadSeekersFor(List<JobApplication> applications) {
        List<String> seekerIds = applications.stream().map(JobApplication::getJobSeekerId).distinct().toList();
        return userRepository.findAllById(seekerIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private String titleOf(Map<String, Job> jobsById, String jobId) {
        Job job = jobsById.get(jobId);
        return job != null ? job.getTitle() : null;
    }
}
