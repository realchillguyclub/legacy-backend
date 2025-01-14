package server.poptato.policy.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.poptato.policy.application.response.PolicyResponseDto;
import server.poptato.policy.converter.PolicyDtoConverter;
import server.poptato.policy.domain.entity.Policy;
import server.poptato.policy.domain.repository.PolicyRepository;
import server.poptato.policy.exception.PolicyException;

import static server.poptato.policy.exception.errorcode.PolicyExceptionErrorCode.POLICY_NOT_FOUND_EXCEPTION;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policyRepository;

    public PolicyResponseDto getPrivacyPolicy() {
        Policy policy = policyRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new PolicyException(POLICY_NOT_FOUND_EXCEPTION));
        return PolicyDtoConverter.toPolicyDto(policy);
    }
}
