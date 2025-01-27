package server.poptato.user.application.response;

import server.poptato.user.domain.entity.User;

public record UserInfoResponseDto(
        String name,
        String email,
        String imageUrl
){

    public static UserInfoResponseDto of(User user) {
        return new UserInfoResponseDto(
                user.getName(),
                user.getEmail(),
                user.getImageUrl()
        );
    }
}
