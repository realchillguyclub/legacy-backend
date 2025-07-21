package server.poptato.user.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.poptato.user.domain.entity.Comment;
import server.poptato.user.domain.repository.CommentRepository;

public interface JpaCommentRepository extends CommentRepository, JpaRepository<Comment, Long> {
}
