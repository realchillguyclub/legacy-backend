package server.poptato.policy.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import server.poptato.policy.application.response.PolicyResponseDto;
import server.poptato.policy.domain.value.Policy;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("unittest")
public class PolicyServiceTest {

    @InjectMocks
    PolicyService policyService;

    @Test
    @DisplayName("[SCN-SVC-POLICY-001][TC-SVC-POLICY-001] 일단의 개인정보 처리방침을 반환한다")
    void getPrivacyPolicy_일단의_개인정보_처리방침_반환() {
        // when
        PolicyResponseDto responseDto = policyService.getPrivacyPolicy();

        // then
        Assertions.assertThat(responseDto.id()).isEqualTo(Policy.PRIVACY_POLICY.getId());
        Assertions.assertThat(responseDto.content()).isEqualTo(Policy.PRIVACY_POLICY.getContent());
        Assertions.assertThat(responseDto.createdAt()).isEqualTo(Policy.PRIVACY_POLICY.getCreatedAt());
    }
}