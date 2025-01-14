package server.poptato.policy.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.poptato.policy.domain.entity.Policy;
import server.poptato.policy.domain.repository.PolicyRepository;

import java.util.Optional;

public interface JpaPolicyRepository extends PolicyRepository, JpaRepository<Policy,Long> {
    Optional<Policy> findTopByOrderByCreatedAtDesc();
}
