package server.poptato.global.convertor;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import server.poptato.user.domain.value.MobileType;

import java.util.stream.Stream;

public class StringToMobileTypeConvertor implements Converter<String, MobileType> {

    @Override
    public MobileType convert(@NotNull String source) {
        return Stream.of(MobileType.values())
                .filter(mobileType -> mobileType.name().equals(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid mobileType: " + source));
    }
}
