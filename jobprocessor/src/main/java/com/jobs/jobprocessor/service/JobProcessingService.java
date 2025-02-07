package com.jobs.jobprocessor.service;

import com.jobs.jobprocessor.model.Task;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobProcessingService {

    public List<Task> sortTasks(List<Task> tasks) throws Exception {
        // A map for quick lookup by task name
        Map<String, Task> taskMap = new HashMap<>();
        for (Task task : tasks) {
            taskMap.put(task.getName(), task);
        }

        // Build the graph
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();
        for (Task task : tasks) {
            String taskName = task.getName();
            inDegree.putIfAbsent(taskName, 0);
            if (task.getRequires() != null) {
                for (String dependency : task.getRequires()) {
                    // Validate dependency exists
                    if (!taskMap.containsKey(dependency)) {
                        throw new Exception("Dependency '" + dependency + "' for task '" + taskName + "' not found.");
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

            List<String> neighbors = graph.getOrDefault(current, new ArrayList<>());
            for (String neighbor : neighbors) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (processed != tasks.size()) {
            throw new Exception("Cycle detected in tasks dependencies.");
        }

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
