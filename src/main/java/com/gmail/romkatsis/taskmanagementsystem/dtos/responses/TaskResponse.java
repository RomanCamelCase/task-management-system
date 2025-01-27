package com.gmail.romkatsis.taskmanagementsystem.dtos.responses;

import com.gmail.romkatsis.taskmanagementsystem.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class TaskResponse {

    private Integer id;

    private String name;

    private String description;

    private TaskStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer ownerId;

    public TaskResponse() {
    }

    public TaskResponse(Integer id, String name, String description, TaskStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, Integer ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.ownerId = ownerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskResponse that = (TaskResponse) o;
        return id.equals(that.id) && name.equals(that.name) && Objects.equals(description, that.description) && status == that.status && createdAt.equals(that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && ownerId.equals(that.ownerId);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + status.hashCode();
        result = 31 * result + createdAt.hashCode();
        result = 31 * result + Objects.hashCode(updatedAt);
        result = 31 * result + ownerId.hashCode();
        return result;
    }
}
