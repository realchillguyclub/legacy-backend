package server.poptato.policy.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import server.poptato.global.response.ApiResponse;
import server.poptato.global.response.status.SuccessStatus;
import server.poptato.policy.application.PolicyService;
import server.poptato.policy.application.response.PolicyResponseDto;

@RestController
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    /**
     * 개인정보 처리방침 조회 API.
     *
     * 사용자가 개인정보 처리방침을 조회할 수 있습니다.
     * 처리방침의 제목과 내용이 포함된 데이터를 반환합니다.
     *
     * @return 개인정보 처리방침 데이터 (제목, 내용)
     */
    @GetMapping("/policy")
    public ResponseEntity<ApiResponse<PolicyResponseDto>> getPolicy(
    ) {
        PolicyResponseDto response = policyService.getPrivacyPolicy();
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }
}
