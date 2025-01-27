package com.gmail.romkatsis.taskmanagementsystem.dtos.requests;

import com.gmail.romkatsis.taskmanagementsystem.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class TaskRequest {

    @NotBlank
    @Size(min = 4, max = 128)
    @Pattern(regexp = "^[A-Za-z0-9]*$")
    private String name;

    private String description = "";

    private TaskStatus status;

    public TaskRequest() {
    }

    public TaskRequest(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public @NotBlank @Size(min = 4, max = 128) @Pattern(regexp = "^[A-Za-z0-9]*$") String getName() {
        return name;
    }

    public void setName(@NotBlank @Size(min = 4, max = 128) @Pattern(regexp = "^[A-Za-z0-9]*$") String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskRequest that = (TaskRequest) o;
        return name.equals(that.name) && Objects.equals(description, that.description) && status == that.status;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + Objects.hashCode(status);
        return result;
    }
}
