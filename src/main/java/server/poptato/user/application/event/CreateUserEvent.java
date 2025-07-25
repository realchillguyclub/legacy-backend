package server.poptato.user.application.event;

import server.poptato.user.domain.entity.User;

public record CreateUserEvent(
        long userCount,
        String userName,
        String socialType,
        String mobileType

) {
    public static CreateUserEvent from(long userCount, User user, String mobileType) {
        return new CreateUserEvent(
                userCount,
                user.getName(),
                user.getSocialType().toString(),
                mobileType
        );
    }
}
