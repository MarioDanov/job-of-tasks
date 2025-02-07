package com.jobs.jobprocessor.exceptions;

public class TaskDependencyException extends RuntimeException {
    public TaskDependencyException(String message) {
        super(message);
    }
}
