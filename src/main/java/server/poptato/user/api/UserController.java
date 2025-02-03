package server.poptato.user.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.poptato.auth.application.service.JwtService;
import server.poptato.global.response.ApiResponse;
import server.poptato.global.response.status.SuccessStatus;
import server.poptato.user.api.request.UserDeleteRequestDTO;
import server.poptato.user.application.response.UserInfoResponseDto;
import server.poptato.user.application.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    /**
     * 사용자 탈퇴 API.
     *
     * 사용자가 요청한 계정 탈퇴를 처리합니다.
     * 요청 헤더에서 JWT 토큰을 사용하여 사용자 ID를 추출하며,
     * 요청 본문으로 탈퇴 사유 또는 추가 인증 정보를 전달받습니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param requestDTO 탈퇴 요청 정보 (탈퇴 사유 등)
     * @return 성공 여부를 나타내는 응답
     */
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<SuccessStatus>> deleteUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UserDeleteRequestDTO requestDTO
    ) {
        userService.deleteUser(jwtService.extractUserIdFromToken(authorizationHeader), requestDTO);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    /**
     * 사용자 정보 조회 API.
     *
     * 사용자의 마이페이지 정보를 반환합니다.
     * 요청 헤더에서 JWT 토큰을 사용하여 사용자 ID를 추출하며,
     * 반환 데이터는 사용자 이름, 이메일, 알림 설정 등을 포함합니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @return 사용자 정보 DTO를 포함한 응답
     */
    @GetMapping("/mypage")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> getUserInfo(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UserInfoResponseDto response = userService.getUserInfo(jwtService.extractUserIdFromToken(authorizationHeader));
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }
}
