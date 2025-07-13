package server.poptato.user.application.event;

import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.value.SocialType;

public record CreateUserEvent(
        long userCount,
        String userName,
        SocialType socialType
) {
    public static CreateUserEvent from(long userCount, User user) {
        return new CreateUserEvent(
                userCount,
                user.getName(),
                user.getSocialType()
        );
    }
}
