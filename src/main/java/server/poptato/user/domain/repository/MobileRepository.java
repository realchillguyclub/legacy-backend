package server.poptato.user.domain.repository;

import server.poptato.user.domain.entity.Mobile;

import java.util.Optional;

public interface MobileRepository {
    Mobile save(Mobile mobile);
    void deleteAllByUserId(Long userId);
    void deleteByClientId(String clientId);
    Optional<Mobile> findByUserId(Long userId);
}
