package server.poptato.user.application.response;

import lombok.Builder;

@Builder
public record UserInfoResponseDto(String name, String email, String imageUrl){
}
