package server.poptato.external.discord.formatter;

import server.poptato.user.application.event.CreateUserCommentEvent;
import server.poptato.user.application.event.CreateUserEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiscordMessageFormatter {

    private static final String CREATE_USER_COMMENT_MESSAGE_TEMPLATE =
            "```[일단에게 의견이 전송되었어요 💌]\n\n" +
            "- 전송 일자 : %s\n" +
            "- 유저 이름 : %s\n" +
            "- 연락처 : %s\n" +
            "- 의견 내용 : \n%s\n```";

    private static final String CREATE_USER_MESSAGE_TEMPLATE =
            "```[일단에 %d번째 유저가 가입했어요 👋🏻]\n\n" +
                    "- 가입 일자 : %s\n" +
                    "- 유저 이름 : %s\n" +
                    "- 소셜 플랫폼 : %s\n```";

    public static String formatCreateUserComment(CreateUserCommentEvent event) {
        String contact = event.contactInfo() == null ? "없음" : event.contactInfo();
        return String.format(
                CREATE_USER_COMMENT_MESSAGE_TEMPLATE,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.userName(),
                contact,
                event.content()
        );
    }

    public static String formatCreateUser(CreateUserEvent event) {
        return String.format(
                CREATE_USER_MESSAGE_TEMPLATE,
                event.userCount(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.userName(),
                event.socialType()
        );
    }
}
