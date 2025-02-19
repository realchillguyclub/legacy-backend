package server.poptato.user.domain.repository;

import server.poptato.user.domain.entity.Mobile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MobileRepository {
    Mobile save(Mobile mobile);
    void deleteAllByUserId(Long userId);
    void deleteByClientId(String clientId);
    List<Mobile> findAllByUserId(Long userId);
    Optional<Mobile> findByClientId(String clientId);
    int deleteOldTokens(LocalDateTime localDateTime);
}
