package com.gmail.romkatsis.taskmanagementsystem.services;

import com.gmail.romkatsis.taskmanagementsystem.exceptions.EmailAlreadyRegisteredException;
import com.gmail.romkatsis.taskmanagementsystem.exceptions.ResourceNotFoundException;
import com.gmail.romkatsis.taskmanagementsystem.models.User;
import com.gmail.romkatsis.taskmanagementsystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User by id %d not found".formatted(id)));
    }

    @Transactional
    public void saveUser(User user) {
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new EmailAlreadyRegisteredException("Email %s already registered".formatted(user.getEmail()));
        }
    }
}
