package com.careernest.backend.service;

import com.careernest.backend.dto.response.ApplicationResponse;
import com.careernest.backend.model.JobApplication.ApplicationStatus;

import java.util.List;

public interface JobApplicationService {

    ApplicationResponse apply(String jobId);

    List<ApplicationResponse> listMyApplications();

    List<ApplicationResponse> listApplicationsForJob(String jobId);

    ApplicationResponse updateStatus(String applicationId, ApplicationStatus status);
}
