package org.eduscript.repositories;

import java.util.UUID;

import org.eduscript.model.JobSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSessionRepository extends CrudRepository<JobSession, UUID> {
}
