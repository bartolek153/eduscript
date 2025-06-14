package org.eduscript.services.impl;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import org.eduscript.model.JobSession;
import org.eduscript.model.UserSession;
import org.eduscript.repositories.JobSessionRepository;
import org.eduscript.repositories.UserSessionRepository;
import org.eduscript.services.UserSessionService;
import org.eduscript.utils.InstanceRegistration;
import org.eduscript.utils.Utils;
import org.springframework.stereotype.Service;

@Service
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;
    private final JobSessionRepository jobSessionRepository;

    public UserSessionServiceImpl(
            UserSessionRepository userSessionRepository,
            JobSessionRepository jobSessionRepository) {
        this.userSessionRepository = userSessionRepository;
        this.jobSessionRepository = jobSessionRepository;
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

    @Override
    public void assignRunningJob(Principal user, UUID jobId) {
        UUID userId = getUserId(user);

        jobSessionRepository.save(
                new JobSession(jobId, userId));
    }

    private UUID getUserId(Principal user) {
        return Utils.uuidToStr(user.getName());
    }
}
