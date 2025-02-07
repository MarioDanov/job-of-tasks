package com.jobs.jobprocessor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class Task {

    @NotBlank(message = "Task name cannot be blank.")
    private String name;

    @NotBlank(message = "Command cannot be blank.")
    private String command;

    @JsonIgnore
    private List<String> requires;

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