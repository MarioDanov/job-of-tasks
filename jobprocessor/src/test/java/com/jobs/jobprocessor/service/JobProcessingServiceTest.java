package com.jobs.jobprocessor.service;

import com.jobs.jobprocessor.exceptions.TaskDependencyException;
import com.jobs.jobprocessor.model.Task;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JobProcessingServiceTest {

    private final JobProcessingService jobProcessingService = new JobProcessingService();

    @Test
    public void testSortTasksSuccess() {
        // task1 requires task2, so the sorted order should be: task2, then task1.
        Task task1 = new Task();
        task1.setName("task1");
        task1.setCommand("echo Task 1");
        task1.setRequires(Arrays.asList("task2"));

        Task task2 = new Task();
        task2.setName("task2");
        task2.setCommand("echo Task 2");

        List<Task> tasks = Arrays.asList(task1, task2);

        List<Task> sortedTasks = jobProcessingService.sortTasks(tasks);

        assertEquals(2, sortedTasks.size());
        assertEquals("task2", sortedTasks.get(0).getName());
        assertEquals("task1", sortedTasks.get(1).getName());
    }

    @Test
    public void testSortTasksMissingDependency() {
        // task1 depends on task2 which is missing.
        Task task1 = new Task();
        task1.setName("task1");
        task1.setCommand("echo Task 1");
        task1.setRequires(Arrays.asList("task2"));

        List<Task> tasks = Arrays.asList(task1);

        TaskDependencyException exception = assertThrows(TaskDependencyException.class,
                () -> jobProcessingService.sortTasks(tasks));
        assertTrue(exception.getMessage().contains("Dependency 'task2' for task 'task1' not found."));
    }

    @Test
    public void testSortTasksCycleDetection() {
        // Create a cycle: task1 requires task2 and task2 requires task1.
        Task task1 = new Task();
        task1.setName("task1");
        task1.setCommand("echo Task 1");
        task1.setRequires(Arrays.asList("task2"));

        Task task2 = new Task();
        task2.setName("task2");
        task2.setCommand("echo Task 2");
        task2.setRequires(Arrays.asList("task1"));

        List<Task> tasks = Arrays.asList(task1, task2);

        TaskDependencyException exception = assertThrows(TaskDependencyException.class,
                () -> jobProcessingService.sortTasks(tasks));
        assertTrue(exception.getMessage().contains("Cycle detected in task dependencies."));
    }
}
