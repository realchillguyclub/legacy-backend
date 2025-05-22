package server.poptato.policy.application.response;

import lombok.Builder;
import server.poptato.policy.domain.value.Policy;

@Builder
public record PolicyResponseDto(
        String content
) {

    public static PolicyResponseDto from(Policy policy) {
        return new PolicyResponseDto(
                policy.getContent()
        );
    }
}
