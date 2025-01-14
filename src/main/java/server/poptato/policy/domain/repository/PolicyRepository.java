package server.poptato.policy.domain.repository;

import server.poptato.policy.domain.entity.Policy;

import java.util.Optional;

public interface PolicyRepository {
    Optional<Policy> findTopByOrderByCreatedAtDesc();
}
