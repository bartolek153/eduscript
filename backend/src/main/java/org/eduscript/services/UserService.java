package org.eduscript.services;

import java.util.Optional;
import java.util.UUID;

import org.eduscript.model.User;

public interface UserService {
    Optional<User> getUser(UUID id);

    UUID registerUser(UUID sessionId);
    
    void updateUserConnection(User user);

    void assignRunningJob(UUID userId, UUID jobId);
}
