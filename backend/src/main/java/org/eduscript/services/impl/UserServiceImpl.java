package org.eduscript.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.eduscript.model.User;
import org.eduscript.repositories.UserRepository;
import org.eduscript.services.UserService;
import org.eduscript.utils.InstanceIdentificator;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getUser(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public UUID registerUser(UUID sessionId) {
        UUID newId = UUID.randomUUID();

        userRepository.save(
                new User(
                        InstanceIdentificator.getId(),
                        sessionId,
                        null));

        return newId;
    }

    @Override
    public void updateUserConnection(User user) {
        userRepository.save(user);
    }

    @Override
    public void assignRunningJob(UUID userId, UUID jobId) {

    }
}
