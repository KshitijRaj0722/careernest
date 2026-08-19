package com.careernest.backend.dto.response;

import com.careernest.backend.model.Job;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private String id;
    private String title;
    private String description;
    private String location;
    private Double salary;
    private LocalDate deadline;
    private String employerId;

    public static JobResponse from(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .salary(job.getSalary())
                .deadline(job.getDeadline())
                .employerId(job.getEmployerId())
                .build();
    }
}
