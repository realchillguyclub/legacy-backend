package server.poptato.policy.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.poptato.global.exception.CustomException;
import server.poptato.policy.application.response.PolicyResponseDto;
import server.poptato.policy.domain.entity.Policy;
import server.poptato.policy.domain.repository.PolicyRepository;
import server.poptato.policy.status.PolicyErrorStatus;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;

    /**
     * 최신 개인정보 처리방침 조회 메서드.
     *
     * 데이터베이스에서 최신 작성된 개인정보 처리방침을 조회하여 반환합니다.
     * 만약 정책이 존재하지 않는 경우, 예외를 발생시킵니다.
     *
     * @return 최신 개인정보 처리방침 데이터를 포함한 DTO
     * @throws CustomException 정책이 존재하지 않을 경우 예외를 발생시킵니다.
     */
    public PolicyResponseDto getPrivacyPolicy() {
        Policy policy = policyRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new CustomException(PolicyErrorStatus._POLICY_NOT_FOUND_EXCEPTION));
        return PolicyResponseDto.from(policy);
    }
}
