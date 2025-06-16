package org.eduscript.services;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import org.eduscript.model.UserSession;

public interface UserSessionService {
    Optional<UserSession> getUser(UUID id);

    UUID saveSession(Principal user, UUID sessionId);
}
