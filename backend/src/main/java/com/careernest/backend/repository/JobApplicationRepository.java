package com.careernest.backend.repository;

import com.careernest.backend.model.JobApplication;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JobApplicationRepository extends MongoRepository<JobApplication, String> {

    List<JobApplication> findByJobSeekerId(String jobSeekerId);

    List<JobApplication> findByJobId(String jobId);

    boolean existsByJobIdAndJobSeekerId(String jobId, String jobSeekerId);
}
