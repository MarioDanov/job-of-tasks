package com.jobs.jobprocessor.service;

import com.jobs.jobprocessor.exceptions.TaskDependencyException;
import com.jobs.jobprocessor.model.Task;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class JobProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(JobProcessingService.class);

    public List<Task> sortTasks(List<Task> tasks) {
        logger.info("Starting task sorting process");

        Map<String, Task> taskMap = new HashMap<>();
        for (Task task : tasks) {
            taskMap.put(task.getName(), task);
        }

        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();

        for (Task task : tasks) {
            String taskName = task.getName();
            inDegree.putIfAbsent(taskName, 0);

            if (task.getRequires() != null) {
                for (String dependency : task.getRequires()) {
                    if (!taskMap.containsKey(dependency)) {
                        logger.error("Dependency '{}' for task '{}' not found.", dependency, taskName);
                        throw new TaskDependencyException("Dependency '" + dependency + "' for task '" + taskName + "' not found.");
                    }
                    graph.computeIfAbsent(dependency, k -> new ArrayList<>()).add(taskName);
                    inDegree.put(taskName, inDegree.getOrDefault(taskName, 0) + 1);
                }
            }
        }

        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<Task> sortedTasks = new ArrayList<>();
        int processed = 0;

        while (!queue.isEmpty()) {
            String current = queue.poll();
            sortedTasks.add(taskMap.get(current));
            processed++;

            for (String neighbor : graph.getOrDefault(current, new ArrayList<>())) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (processed != tasks.size()) {
            logger.error("Cycle detected in task dependencies.");
            throw new TaskDependencyException("Cycle detected in task dependencies.");
        }

        logger.info("Task sorting completed successfully.");
        return sortedTasks;
    }

    public String generateBashScript(List<Task> sortedTasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("#!/usr/bin/env bash").append(System.lineSeparator());
        for (Task task : sortedTasks) {
            sb.append(task.getCommand()).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
