package org.eduscript.repositories;

import java.util.UUID;

import org.eduscript.model.Instance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstanceRepository extends CrudRepository<Instance, UUID> {
}
