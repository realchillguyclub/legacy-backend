package server.poptato.user.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.poptato.user.domain.entity.DeleteReason;
import server.poptato.user.domain.repository.DeleteReasonRepository;

public interface JpaDeleteReasonRepository extends DeleteReasonRepository, JpaRepository<DeleteReason, Long> {
}
