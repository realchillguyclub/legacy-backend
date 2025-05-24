package server.poptato.external.firebase.template;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FcmNotificationTemplate {

    START_OF_DAY("일단 시작할 시간", "할 일을 계획하고 하루를 시작하세요!"),
    END_OF_DAY("하루 돌아보기", "완료한 일을 체크하세요"),
    DEADLINE("오늘 마감 예정인 할 일", "%s"),
    TIME_DEADLINE("할 일을 미리 준비하세요", "%s");

    private final String title;
    private final String body;
}
