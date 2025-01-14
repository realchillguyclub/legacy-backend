package server.poptato.policy.converter;

import org.springframework.stereotype.Component;
import server.poptato.policy.application.response.PolicyResponseDto;
import server.poptato.policy.domain.entity.Policy;

@Component
public class PolicyDtoConverter {

    public static PolicyResponseDto toPolicyDto(Policy policy) {
        return PolicyResponseDto.builder()
                .id(policy.getId())
                .content(policy.getContent())
                .createdAt(policy.getCreatedAt())
                .build();

    }

    private PolicyDtoConverter() {
    }
}

