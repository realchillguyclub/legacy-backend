package server.poptato.external.firebase.template;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FcmNotificationTemplate {

    START_OF_DAY("일단 시작할 시간", "오늘 할 일을 확인하고 하루를 계획하세요!"),
    END_OF_DAY("오늘 돌아보기", "계획한 할 일 중 완료한 것을 체크하세요!"),
    DEADLINE("오늘 마감 예정인 할 일", "%s");

    private final String title;
    private final String body;
}
