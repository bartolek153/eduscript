package org.eduscript.repositories;

import java.util.UUID;

import org.eduscript.model.JobMetadata;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobMetadataRepository extends CrudRepository<JobMetadata, UUID> {
}
