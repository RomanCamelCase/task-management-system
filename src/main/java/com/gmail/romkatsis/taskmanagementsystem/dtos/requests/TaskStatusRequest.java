package com.gmail.romkatsis.taskmanagementsystem.dtos.requests;

import com.gmail.romkatsis.taskmanagementsystem.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public class TaskStatusRequest {

    @NotNull
    private TaskStatus status;

    public TaskStatusRequest() {}

    public @NotNull TaskStatus getStatus() {
        return status;
    }

    public void setStatus(@NotNull TaskStatus status) {
        this.status = status;
    }
}
