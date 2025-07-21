package server.poptato.user.domain.repository;

import server.poptato.user.domain.entity.Comment;

public interface CommentRepository {

    Comment save(Comment comment);
}
