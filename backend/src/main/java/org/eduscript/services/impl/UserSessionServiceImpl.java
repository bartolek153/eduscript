package org.eduscript.services.impl;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import org.eduscript.model.UserSession;
import org.eduscript.repositories.UserSessionRepository;
import org.eduscript.services.UserSessionService;
import org.eduscript.utils.InstanceRegistration;
import org.eduscript.utils.Utils;
import org.springframework.stereotype.Service;

@Service
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;

    public UserSessionServiceImpl(
            UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }

    @Override
    public Optional<UserSession> getUser(UUID id) {
        return userSessionRepository.findById(id);
    }

    @Override
    public UUID saveSession(Principal user, UUID sessionId) {
        UUID userId = getUserId(user);

        userSessionRepository.save(
                new UserSession(
                        userId,
                        InstanceRegistration.getId(),
                        sessionId));

        return userId;
    }

    private UUID getUserId(Principal user) {
        return Utils.uuidToStr(user.getName());
    }
}
