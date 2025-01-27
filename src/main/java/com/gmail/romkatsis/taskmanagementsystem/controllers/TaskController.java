package com.gmail.romkatsis.taskmanagementsystem.controllers;

import com.gmail.romkatsis.taskmanagementsystem.dtos.requests.TaskRequest;
import com.gmail.romkatsis.taskmanagementsystem.dtos.requests.TaskStatusRequest;
import com.gmail.romkatsis.taskmanagementsystem.dtos.responses.TaskResponse;
import com.gmail.romkatsis.taskmanagementsystem.enums.TaskStatus;
import com.gmail.romkatsis.taskmanagementsystem.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponse> getAllTasks(@RequestParam(required = false) TaskStatus status) {
        if (Objects.isNull(status)) {
            return taskService.getAllTasks();
        }
        return taskService.getAllTasksByStatus(status);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse getTaskById(@PathVariable() int id) {
        return taskService.findTaskById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestBody @Valid TaskRequest taskRequest) {
        return taskService.addTask(Integer.valueOf(userDetails.getUsername()), taskRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@taskService.isUserHasPermissionToUpdateTask(authentication, #id)")
    public TaskResponse updateTask(@PathVariable() int id, @RequestBody @Valid TaskRequest taskRequest) {
        return taskService.updateTask(id, taskRequest);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@taskService.isUserHasPermissionToUpdateTask(authentication, #id)")
    public TaskResponse updateTaskStatus(@PathVariable() int id, @RequestBody @Valid TaskStatusRequest status) {
        return taskService.setTaskStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable() int id) {
        taskService.deleteTask(id);
    }
}
