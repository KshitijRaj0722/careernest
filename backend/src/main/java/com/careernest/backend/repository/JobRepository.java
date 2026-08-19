package com.careernest.backend.repository;

import com.careernest.backend.model.Job;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JobRepository extends MongoRepository<Job, String> {

    List<Job> findByEmployerId(String employerId);

    // Combined location+keyword search is built dynamically in JobServiceImpl via
    // MongoTemplate, since both filters are optional.
}
