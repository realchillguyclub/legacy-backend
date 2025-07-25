package server.poptato.external.discord.formatter;

import server.poptato.user.application.event.CreateUserCommentEvent;
import server.poptato.user.application.event.CreateUserEvent;
import server.poptato.user.application.event.DeleteUserEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DiscordMessageFormatter {

    private static final String CREATE_USER_COMMENT_MESSAGE_TEMPLATE =
            "```[일단에게 의견이 전송되었어요 💌]\n\n" +
            "- 전송 일자 : %s\n" +
            "- 유저 이름 : %s\n" +
            "- 연락처 : %s\n" +
            "- 디바이스 : %s\n" +
            "- 의견 내용 : %s\n```";

    private static final String CREATE_USER_MESSAGE_TEMPLATE =
            "```[일단에 %d번째 유저가 가입했어요 👋🏻]\n\n" +
                    "- 가입 일자 : %s\n" +
                    "- 유저 이름 : %s\n" +
                    "- 디바이스 : %s\n" +
                    "- 소셜 플랫폼 : %s\n```";

    private static final String DELETE_USER_MESSAGE_TEMPLATE =
            "```[유저가 일단을 떠났어요 🥲]\n\n" +
                    "- 가입 일자 : %s\n" +
                    "- 탈퇴 일자 : %s\n" +
                    "- 유저 이름 : %s\n" +
                    "- 디바이스 : %s\n" +
                    "- 소셜 플랫폼 : %s\n" +
                    "- 탈퇴 사유 :\n%s\n```";

    public static String formatCreateUserCommentMessage(CreateUserCommentEvent event) {
        String contact = event.contactInfo() == null ? "없음" : event.contactInfo();
        return String.format(
                CREATE_USER_COMMENT_MESSAGE_TEMPLATE,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.userName(),
                contact,
                event.mobileType(),
                event.content()
        );
    }

    public static String formatCreateUserMessage(CreateUserEvent event) {
        return String.format(
                CREATE_USER_MESSAGE_TEMPLATE,
                event.userCount(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.userName(),
                event.mobileType(),
                event.socialType()
        );
    }

    public static String formatDeleteUserMessage(DeleteUserEvent event) {
        String formattedReasons = formatDeleteReasons(event.deleteReasons());

        return String.format(
                DELETE_USER_MESSAGE_TEMPLATE,
                event.createDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.userName(),
                event.mobileType(),
                event.socialType(),
                formattedReasons
        );
    }

    private static String formatDeleteReasons(List<String> reasons) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reasons.size(); i++) {
            sb.append(i + 1).append(". ").append(reasons.get(i)).append("\n");
        }
        return sb.toString().trim();
    }
}
