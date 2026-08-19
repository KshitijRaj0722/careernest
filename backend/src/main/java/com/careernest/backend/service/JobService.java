package com.careernest.backend.service;

import com.careernest.backend.dto.request.JobRequest;
import com.careernest.backend.dto.response.JobResponse;

import java.util.List;

public interface JobService {

    JobResponse create(JobRequest request);

    JobResponse update(String jobId, JobRequest request);

    void delete(String jobId);

    JobResponse getById(String jobId);

    /** Both filters are optional; null/blank means "no filter on that field". */
    List<JobResponse> search(String location, String keyword);

    List<JobResponse> listMyJobs();
}
