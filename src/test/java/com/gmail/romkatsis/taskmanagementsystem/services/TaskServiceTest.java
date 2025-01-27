package com.gmail.romkatsis.taskmanagementsystem.services;

import com.gmail.romkatsis.taskmanagementsystem.dtos.requests.TaskRequest;
import com.gmail.romkatsis.taskmanagementsystem.dtos.responses.TaskResponse;
import com.gmail.romkatsis.taskmanagementsystem.enums.TaskStatus;
import com.gmail.romkatsis.taskmanagementsystem.exceptions.ResourceNotFoundException;
import com.gmail.romkatsis.taskmanagementsystem.models.Task;
import com.gmail.romkatsis.taskmanagementsystem.models.User;
import com.gmail.romkatsis.taskmanagementsystem.repositories.TaskRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Spy
    private ModelMapper modelMapper;

    @Mock
    private UserService userService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TaskService taskService;

    private TaskRequest mockTaskRequest;

    private TaskResponse mockTaskResponse;

    private List<Task> mockTaskList;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        mockTaskRequest = new TaskRequest(
                "task",
                "description",
                TaskStatus.IN_PROGRESS
        );
        Task mockTask1 = new Task(
                1,
                "task",
                "description",
                TaskStatus.IN_PROGRESS,
                now,
                null,
                mock(User.class)
        );
        Task mockTask2 = new Task(
                2,
                "task",
                "description",
                TaskStatus.OPEN,
                now,
                null,
                mock(User.class)
        );
        mockTaskResponse = new TaskResponse(
                1,
                "task",
                "description",
                TaskStatus.IN_PROGRESS,
                now,
                null,
                0
        );
        mockTaskList = new ArrayList<>(List.of(mockTask1, mockTask2));
    }

    @Test
    void getAllTasks_ReturnsListOfTaskResponses() {
        when(taskRepository.findAll()).thenReturn(mockTaskList);

        List<TaskResponse> taskResponses = taskService.getAllTasks();

        assertNotNull(taskResponses);
        assertEquals(taskResponses.size(), 2);
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getAllTasksByStatus_ReturnsFilteredTaskResponses() {
        when(taskRepository.findAllByStatus(TaskStatus.IN_PROGRESS))
                .thenReturn(new ArrayList<>(Collections.singletonList(mockTaskList.getFirst())));

        List<TaskResponse> taskResponses = taskService.getAllTasksByStatus(TaskStatus.IN_PROGRESS);

        assertNotNull(taskResponses);
        assertEquals(taskResponses.size(), 1);
        verify(taskRepository, times(1)).findAllByStatus(TaskStatus.IN_PROGRESS);
    }

    @Test
    void findTaskById_TaskExists_ReturnsTaskResponse() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(mockTaskList.getFirst()));

        TaskResponse taskResponse = taskService.findTaskById(1);

        assertEquals(taskResponse, mockTaskResponse);
        verify(taskRepository, times(1)).findById(1);
    }

    @Test
    void findTaskById_TaskDoesNotExist_ThrowsException() {
        int taskId = 1;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class, () -> taskService.findTaskById(taskId));

        assertEquals("Task by id 1 not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void addTask_ValidRequest_SavesTask() {
        int userId = 1;
        when(userService.findUserById(userId)).thenReturn(mock(User.class));

        TaskResponse taskResponse = taskService.addTask(userId, mockTaskRequest);

        assertTrue(
                mockTaskResponse.getName().equals(taskResponse.getName())
                        && mockTaskResponse.getDescription().equals(taskResponse.getDescription())
                        && mockTaskResponse.getStatus().equals(taskResponse.getStatus())
        );
        verify(userService, times(1)).findUserById(userId);
        verify(entityManager, times(1)).flush();
    }

    @Test
    void updateTask_ValidRequest_UpdatesTask() {
        int taskId = 1;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTaskList.getFirst()));

        TaskResponse taskResponse = taskService.updateTask(taskId, mockTaskRequest);

        assertNotNull(taskResponse);
        assertNotNull(taskResponse.getUpdatedAt());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void deleteTask_TaskExists_DeletesTask() {
        int taskId = 1;
        Task mockTask = mock(Task.class);
        User mockUser = mock(User.class);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTask));
        when(mockTask.getOwner()).thenReturn(mockUser);

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).findById(taskId);
        verify(mockTask, times(1)).getOwner();
        verify(mockUser, times(1)).deleteTask(mockTask);
    }
}