package com.jobs.jobprocessor.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class Job {
    @NotEmpty(message = "Tasks list cannot be empty.")
    @Valid
    private List<Task> tasks;

    public Job() {}

    public Job(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}