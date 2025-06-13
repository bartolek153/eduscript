package org.eduscript.repositories;

import java.util.UUID;

import org.eduscript.model.UserSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends CrudRepository<UserSession, UUID> {
}
