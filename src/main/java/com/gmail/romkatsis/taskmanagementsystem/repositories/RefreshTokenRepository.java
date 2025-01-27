package com.gmail.romkatsis.taskmanagementsystem.repositories;

import com.gmail.romkatsis.taskmanagementsystem.models.RefreshToken;
import com.gmail.romkatsis.taskmanagementsystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);

    void deleteAllByUser(User user);
}
