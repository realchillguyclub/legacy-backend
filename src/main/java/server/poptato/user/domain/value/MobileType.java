package server.poptato.user.domain.value;

import lombok.Getter;

@Getter
public enum MobileType {
    ANDROID(".sgv"),
    IOS(".pdf");

    private final String imageUrlExtension;

    MobileType(String imageUrlExtension) {
        this.imageUrlExtension = imageUrlExtension;
    }

}
