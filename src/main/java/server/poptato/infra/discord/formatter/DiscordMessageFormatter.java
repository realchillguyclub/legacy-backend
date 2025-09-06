package server.poptato.infra.discord.formatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import server.poptato.infra.discord.dto.DailyStats;
import server.poptato.user.application.event.CreateUserCommentEvent;
import server.poptato.user.application.event.CreateUserEvent;
import server.poptato.user.application.event.DeleteUserEvent;

public class DiscordMessageFormatter {

    private static final String CREATE_USER_COMMENT_MESSAGE_TEMPLATE =
            "```[일단에게 의견이 전송되었어요 💌]\n\n" +
            "- 전송 일자: %s\n" +
            "- 유저 이름: %s\n" +
            "- 연락처: %s\n" +
            "- 디바이스: %s\n" +
            "- 의견 내용: %s\n```";

    private static final String CREATE_USER_MESSAGE_TEMPLATE =
            "```[일단에 %d번째 유저가 가입했어요 👋🏻]\n\n" +
                    "- 가입 일자: %s\n" +
                    "- 유저 이름: %s\n" +
                    "- 디바이스: %s\n" +
                    "- 소셜 플랫폼: %s\n```";

    private static final String DELETE_USER_MESSAGE_TEMPLATE =
            "```[유저가 일단을 떠났어요 🥲]\n\n" +
                    "- 가입 일자: %s\n" +
                    "- 탈퇴 일자: %s\n" +
                    "- 유저 이름: %s\n" +
                    "- 디바이스: %s\n" +
                    "- 소셜 플랫폼: %s\n" +
                    "- 탈퇴 사유:\n%s\n```";

	private static final String DAILY_STATS_MESSAGE_TEMPLATE =
		"```[일일 통계 보고서 📊]\n\n" +
			"- 기준 일자: %s\n" +
			"- 신규 가입자: 전체 %d명 (iOS %d명 / Android %d명)\n" +
			"- 오늘 생성된 할 일: %d개\n" +
			"- 오늘 할 일 완료 비율(전체): %d/%d (%.2f%%)\n```";

    private static final String FORMATTED_DATE_TIME = "yyyy-MM-dd HH:mm";

    private DiscordMessageFormatter() {
    }

	public static String formatCreateUserCommentMessage(CreateUserCommentEvent event) {
        String contact = event.contactInfo() == null ? "없음" : event.contactInfo();
        return String.format(
                CREATE_USER_COMMENT_MESSAGE_TEMPLATE,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(FORMATTED_DATE_TIME)),
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
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(FORMATTED_DATE_TIME)),
                event.userName(),
                event.mobileType(),
                event.socialType()
        );
    }

    public static String formatDeleteUserMessage(DeleteUserEvent event) {
        String formattedReasons = formatDeleteReasons(event.deleteReasons());

        return String.format(
                DELETE_USER_MESSAGE_TEMPLATE,
                event.createDate().format(DateTimeFormatter.ofPattern(FORMATTED_DATE_TIME)),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(FORMATTED_DATE_TIME)),
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

	public static String formatDailyStatsMessage(DailyStats s) {
		return String.format(
			DAILY_STATS_MESSAGE_TEMPLATE,
			s.targetDate(),
			s.signupsTotal(), s.signupsIos(), s.signupsAndroid(),
			s.todosCreated(),
			s.todayCompleted(), s.todayTotal(), s.todayCompletedRate()
		);
	}
}
