package server.poptato.user.infra.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.repository.UserRepository;

import java.util.Optional;

public interface JpaUserRepository extends UserRepository, JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.socialId = :socialId")
    Optional<User> findBySocialId(@Param("socialId") String socialId);
}