package server.poptato.user.converter;

import org.springframework.stereotype.Component;
import server.poptato.user.application.response.UserInfoResponseDto;
import server.poptato.user.domain.entity.User;

@Component
public class UserDtoConverter {

    public static UserInfoResponseDto toUserInfoDto(User user) {
        return UserInfoResponseDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .build();
    }

    private UserDtoConverter() {
    }
}
