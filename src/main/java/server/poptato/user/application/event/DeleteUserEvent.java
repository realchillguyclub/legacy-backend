package server.poptato.user.application.event;

import server.poptato.user.domain.entity.Mobile;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.value.Reason;

import java.util.List;

public record DeleteUserEvent(
        String userName,
        String mobileType,
        String socialType,
        String createDate,
        List<String> deleteReasons

) {
    public static DeleteUserEvent from(User user, Mobile mobile, List<Reason> deleteReasons) {
        return new DeleteUserEvent(
                user.getName(),
                user.getSocialType().toString(),
                mobile.getType().toString(),
                user.getCreateDate().toString(),
                deleteReasons.stream()
                        .map(Reason::getValue)
                        .toList()
        );
    }
}
