package com.careernest.backend.service.impl;

import com.careernest.backend.dto.request.JobRequest;
import com.careernest.backend.dto.response.JobResponse;
import com.careernest.backend.exception.ForbiddenOperationException;
import com.careernest.backend.exception.ResourceNotFoundException;
import com.careernest.backend.model.Job;
import com.careernest.backend.model.User;
import com.careernest.backend.repository.JobRepository;
import com.careernest.backend.security.CurrentUserProvider;
import com.careernest.backend.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final MongoTemplate mongoTemplate;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public JobResponse create(JobRequest request) {
        User employer = currentUserProvider.requireCurrentUser();

        Job job = jobRepository.save(Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .salary(request.getSalary())
                .deadline(request.getDeadline())
                .employerId(employer.getId())
                .build());

        return JobResponse.from(job);
    }

    @Override
    public JobResponse update(String jobId, JobRequest request) {
        Job job = requireOwnedJob(jobId);

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setDeadline(request.getDeadline());

        return JobResponse.from(jobRepository.save(job));
    }

    @Override
    public void delete(String jobId) {
        jobRepository.delete(requireOwnedJob(jobId));
    }

    @Override
    public JobResponse getById(String jobId) {
        return JobResponse.from(requireJob(jobId));
    }

    @Override
    public List<JobResponse> search(String location, String keyword) {
        List<Criteria> filters = new ArrayList<>();

        // Pattern.quote escapes user input so a search term like ".*" or "(" can't
        // become an expensive/broken regex.
        if (StringUtils.hasText(location)) {
            filters.add(Criteria.where("location").regex(Pattern.quote(location), "i"));
        }
        if (StringUtils.hasText(keyword)) {
            filters.add(new Criteria().orOperator(
                    Criteria.where("title").regex(Pattern.quote(keyword), "i"),
                    Criteria.where("description").regex(Pattern.quote(keyword), "i")));
        }

        Query query = new Query();
        if (!filters.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filters.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Job.class).stream().map(JobResponse::from).toList();
    }

    @Override
    public List<JobResponse> listMyJobs() {
        User employer = currentUserProvider.requireCurrentUser();
        return jobRepository.findByEmployerId(employer.getId()).stream().map(JobResponse::from).toList();
    }

    private Job requireJob(String jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
    }

    /** Employers may only mutate their own postings, even though the role check already passed. */
    private Job requireOwnedJob(String jobId) {
        Job job = requireJob(jobId);
        User employer = currentUserProvider.requireCurrentUser();
        if (!employer.getId().equals(job.getEmployerId())) {
            throw new ForbiddenOperationException("You can only modify job postings you created");
        }
        return job;
    }
}
