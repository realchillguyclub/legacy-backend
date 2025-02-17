package server.poptato.user.domain.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MobileType {
    ANDROID(".svg"),
    IOS(".pdf");

    private final String imageUrlExtension;
}
