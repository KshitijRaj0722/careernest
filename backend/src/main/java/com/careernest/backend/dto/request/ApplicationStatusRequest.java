package com.careernest.backend.dto.request;

import com.careernest.backend.model.JobApplication.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationStatusRequest {

    @NotNull
    private ApplicationStatus status;
}
