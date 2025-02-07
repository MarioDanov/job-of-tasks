package com.jobs.jobprocessor.controller;

import com.jobs.jobprocessor.model.Job;
import com.jobs.jobprocessor.model.Task;
import com.jobs.jobprocessor.service.JobProcessingService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobProcessingService jobProcessingService;

    JobController(JobProcessingService jobProcessingService){
        this.jobProcessingService = jobProcessingService;
    }

    @PostMapping("/process")
    public List<Task> processJob(@RequestBody Job job) throws Exception {
        return jobProcessingService.sortTasks(job.getTasks());
    }

    @PostMapping(value = "/process/script", produces = MediaType.TEXT_PLAIN_VALUE)
    public String processJobAsBashScript(@RequestBody Job job) throws Exception {
        List<Task> sortedTasks = jobProcessingService.sortTasks(job.getTasks());
        return jobProcessingService.generateBashScript(sortedTasks);
    }
}
