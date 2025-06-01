package server.poptato.policy.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.poptato.policy.application.response.PolicyResponseDto;
import server.poptato.policy.domain.value.Policy;

@Service
@RequiredArgsConstructor
public class PolicyService {

    /**
     * 개인정보 처리방침 조회 메서드.
     *
     * @return 최신 개인정보 처리방침 데이터를 포함한 DTO
     */
    public PolicyResponseDto getPrivacyPolicy() {
        return PolicyResponseDto.from(Policy.PRIVACY_POLICY);
    }
}
