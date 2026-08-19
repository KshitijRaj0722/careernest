package com.careernest.backend.dto.response;

import com.careernest.backend.model.JobApplication;
import com.careernest.backend.model.JobApplication.ApplicationStatus;
import com.careernest.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    private String id;
    private String jobId;
    private String jobTitle;
    private String jobSeekerId;
    private String jobSeekerName;
    private String jobSeekerEmail;
    private String jobSeekerPhone;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;

    /**
     * Contact details are included so an employer can reach applicants to their own
     * postings. Access is already scoped: the applicant list endpoints reject anyone
     * who does not own the job, and seekers only ever receive their own record.
     */
    public static ApplicationResponse from(JobApplication application, String jobTitle, User seeker) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJobId())
                .jobTitle(jobTitle)
                .jobSeekerId(application.getJobSeekerId())
                .jobSeekerName(seeker != null ? seeker.getFullName() : null)
                .jobSeekerEmail(seeker != null ? seeker.getEmail() : null)
                .jobSeekerPhone(seeker != null ? seeker.getPhoneNumber() : null)
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}
