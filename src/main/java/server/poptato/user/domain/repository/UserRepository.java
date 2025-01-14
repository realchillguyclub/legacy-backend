package server.poptato.user.domain.repository;

import server.poptato.user.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findBySocialId(String socialId);
    Optional<User> findById(Long userId);
    void delete(User user);
    User save(User user);
    List<User> findAll();
}