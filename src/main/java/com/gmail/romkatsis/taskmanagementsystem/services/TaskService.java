package com.gmail.romkatsis.taskmanagementsystem.services;

import com.gmail.romkatsis.taskmanagementsystem.dtos.requests.TaskRequest;
import com.gmail.romkatsis.taskmanagementsystem.dtos.requests.TaskStatusRequest;
import com.gmail.romkatsis.taskmanagementsystem.dtos.responses.TaskResponse;
import com.gmail.romkatsis.taskmanagementsystem.enums.Role;
import com.gmail.romkatsis.taskmanagementsystem.enums.TaskStatus;
import com.gmail.romkatsis.taskmanagementsystem.exceptions.ResourceNotFoundException;
import com.gmail.romkatsis.taskmanagementsystem.models.Task;
import com.gmail.romkatsis.taskmanagementsystem.models.User;
import com.gmail.romkatsis.taskmanagementsystem.repositories.TaskRepository;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    private final ModelMapper modelMapper;

    private final UserService userService;

    private final EntityManager entityManager;

    @Autowired
    public TaskService(TaskRepository taskRepository, ModelMapper modelMapper, UserService userService, EntityManager entityManager) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.entityManager = entityManager;
    }

    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return convertTaskListToTaskResponseList(tasks);
    }

    public List<TaskResponse> getAllTasksByStatus(TaskStatus status) {
        List<Task> tasks = taskRepository.findAllByStatus(status);
        return convertTaskListToTaskResponseList(tasks);
    }

    public TaskResponse findTaskById(Integer id) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task by id %d not found".formatted(id)));
        return modelMapper.map(task, TaskResponse.class);
    }

    @Transactional
    public TaskResponse addTask(Integer userId, TaskRequest taskRequest) {
        Task task = modelMapper.map(taskRequest, Task.class);
        task.setCreatedAt(LocalDateTime.now());
        if (Objects.isNull(taskRequest.getStatus())) {
            task.setStatus(TaskStatus.OPEN);
        }

        User user = userService.findUserById(userId);
        user.addTask(task);
        entityManager.flush();
        return modelMapper.map(task, TaskResponse.class);
    }

    @Transactional
    public TaskResponse updateTask(int id, TaskRequest taskRequest) {
        Task task = getTaskById(id);
        task.setUpdatedAt(LocalDateTime.now());
        modelMapper.map(taskRequest, task);
        return modelMapper.map(task, TaskResponse.class);
    }

    @Transactional
    public TaskResponse setTaskStatus(int id, TaskStatusRequest request) {
        Task task = getTaskById(id);
        task.setStatus(request.getStatus());
        task.setUpdatedAt(LocalDateTime.now());
        return modelMapper.map(task, TaskResponse.class);
    }

    @Transactional
    public void deleteTask(int id) {
        Task task = getTaskById(id);
        User user = task.getOwner();
        user.deleteTask(task);
    }

    public boolean isUserHasPermissionToUpdateTask(Authentication authentication, Integer taskId) {
        if (authentication.getAuthorities().contains(Role.ROLE_ADMIN)) {
            return true;
        }
        Task task = getTaskById(taskId);
        Integer userId = Integer.valueOf(authentication.getName());
        return task.getOwner().getId().equals(userId);
    }

    private Task getTaskById(Integer id) {
        return taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task by id %d not found".formatted(id)));
    }

    private List<TaskResponse> convertTaskListToTaskResponseList(List<Task> tasks) {
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .toList();
    }
}
