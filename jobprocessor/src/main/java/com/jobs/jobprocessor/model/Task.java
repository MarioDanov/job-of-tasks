package com.jobs.jobprocessor.model;

import java.util.List;

public class Task {
    private String name;
    private String command;
    private List<String> requires;

    public Task() {}

    public Task(String name, String command, List<String> requires) {
        this.name = name;
        this.command = command;
        this.requires = requires;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getRequires() {
        return requires;
    }

    public void setRequires(List<String> requires) {
        this.requires = requires;
    }
}
