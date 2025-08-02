package server.poptato.user.application.event;

import server.poptato.user.domain.entity.Comment;

public record CreateUserCommentEvent(
        Long commentId,
        Long userId,
        String userName,
        String content,
        String contactInfo,
        String mobileType
) {
    public static CreateUserCommentEvent from(Comment comment, String userName, String mobileType) {
        return new CreateUserCommentEvent(
                comment.getId(),
                comment.getUserId(),
                userName,
                comment.getContent(),
                comment.getContactInfo(),
                mobileType
        );
    }
}
