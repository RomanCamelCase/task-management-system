package com.gmail.romkatsis.taskmanagementsystem.repositories;

import com.gmail.romkatsis.taskmanagementsystem.enums.TaskStatus;
import com.gmail.romkatsis.taskmanagementsystem.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findAllByStatus(TaskStatus status);
}
