package server.poptato.user.application.event;

import server.poptato.user.domain.entity.User;

public record CreateUserEvent(
        long userCount,
        String userName,
        String socialType,
        String createDate
) {
    public static CreateUserEvent from(long userCount, User user) {
        return new CreateUserEvent(
                userCount,
                user.getName(),
                user.getSocialId(),
                user.getCreateDate().toString()
        );
    }
}
