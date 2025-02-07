package com.jobs.jobprocessor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobs.jobprocessor.model.Job;
import com.jobs.jobprocessor.model.Task;
import com.jobs.jobprocessor.service.JobProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobController.class)
public class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobProcessingService jobProcessingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testProcessJobEndpoint() throws Exception {
        // Prepare two tasks where task1 depends on task2.
        Task task1 = new Task();
        task1.setName("task1");
        task1.setCommand("echo Task 1");

        Task task2 = new Task();
        task2.setName("task2");
        task2.setCommand("echo Task 2");

        // Assume the service sorts them so that task2 comes before task1.
        List<Task> sortedTasks = Arrays.asList(task2, task1);
        given(jobProcessingService.sortTasks(anyList())).willReturn(sortedTasks);

        Job job = new Job();
        job.setTasks(Arrays.asList(task1, task2));
        String jobJson = objectMapper.writeValueAsString(job);

        mockMvc.perform(post("/api/jobs/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("task2"))
                .andExpect(jsonPath("$[0].command").value("echo Task 2"))
                .andExpect(jsonPath("$[1].name").value("task1"))
                .andExpect(jsonPath("$[1].command").value("echo Task 1"))
                .andExpect(jsonPath("$[0].requires").doesNotExist())
                .andExpect(jsonPath("$[1].requires").doesNotExist());
    }

    @Test
    public void testProcessJobAsBashScriptEndpoint() throws Exception {
        // Prepare two tasks where task1 depends on task2.
        Task task1 = new Task();
        task1.setName("task1");
        task1.setCommand("echo Task 1");

        Task task2 = new Task();
        task2.setName("task2");
        task2.setCommand("echo Task 2");

        List<Task> sortedTasks = Arrays.asList(task2, task1);
        String bashScript = "#!/usr/bin/env bash\necho Task 2\necho Task 1\n";

        given(jobProcessingService.sortTasks(anyList())).willReturn(sortedTasks);
        given(jobProcessingService.generateBashScript(sortedTasks)).willReturn(bashScript);

        Job job = new Job();
        job.setTasks(Arrays.asList(task1, task2));
        String jobJson = objectMapper.writeValueAsString(job);

        mockMvc.perform(post("/api/jobs/process/script")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(bashScript));
    }

    @Test
    public void testValidationErrorEmptyTasks() throws Exception {
        // Sending an empty tasks list should trigger a validation error.
        Job job = new Job();
        job.setTasks(Collections.emptyList());
        String jobJson = objectMapper.writeValueAsString(job);

        mockMvc.perform(post("/api/jobs/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.tasks").value("Tasks list cannot be empty."));
    }

    @Test
    public void testValidationErrorMissingTaskName() throws Exception {
        // A task missing a name should trigger a validation error.
        Task task = new Task();
        // Not setting the name to simulate the error.
        task.setCommand("echo Task 1");

        Job job = new Job();
        job.setTasks(Arrays.asList(task));
        String jobJson = objectMapper.writeValueAsString(job);

        mockMvc.perform(post("/api/jobs/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['tasks[0].name']").value("Task name cannot be blank."));
    }
}

