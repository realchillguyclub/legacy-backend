package server.poptato.user.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.poptato.user.domain.entity.Mobile;
import server.poptato.user.domain.repository.MobileRepository;

public interface JpaMobileRepository extends MobileRepository, JpaRepository<Mobile, Long> {
}
