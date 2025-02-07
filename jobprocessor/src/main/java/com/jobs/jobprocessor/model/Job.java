package com.jobs.jobprocessor.model;

import java.util.List;

public class Job {
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