package server.poptato.user.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.user.domain.entity.Mobile;
import server.poptato.user.domain.repository.MobileRepository;

import java.time.LocalDateTime;

@Transactional
public interface JpaMobileRepository extends MobileRepository, JpaRepository<Mobile, Long> {

    @Modifying
    void deleteByClientId(String clientId);

    @Modifying
    @Query("""
        DELETE FROM Mobile m
        WHERE m.modifyDate < :localDateTime
    """)
    void deleteOldTokens(@Param("localDateTime") LocalDateTime localDateTime);
}
