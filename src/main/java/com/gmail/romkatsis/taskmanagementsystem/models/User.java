package com.gmail.romkatsis.taskmanagementsystem.models;


import com.gmail.romkatsis.taskmanagementsystem.enums.Role;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "username", nullable = false)
    private String username;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "roles",
            joinColumns =@JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();

    public User() {}

    public User(Integer id, String email, String password, String username, Set<Role> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.roles = roles;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
        task.setOwner(this);
    }

    public void deleteTask(Task task) {
        this.tasks.remove(task);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
}
