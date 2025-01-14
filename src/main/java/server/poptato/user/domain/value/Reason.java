package server.poptato.user.domain.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Reason {
    NOT_USED_OFTEN("자주 사용하지 않아요"),
    MISSING_FEATURES("원하는 기능이 없어요"),
    TOO_COMPLEX("과정이 너무 복잡해요");

    private final String value;
}
