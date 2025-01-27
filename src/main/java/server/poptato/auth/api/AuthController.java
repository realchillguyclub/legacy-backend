package server.poptato.auth.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.poptato.auth.api.request.LoginRequestDto;
import server.poptato.auth.api.request.ReissueTokenRequestDto;
import server.poptato.auth.application.response.LoginResponseDto;
import server.poptato.auth.application.service.AuthService;
import server.poptato.global.dto.TokenPair;
import server.poptato.global.response.ApiResponse;
import server.poptato.global.response.status.SuccessStatus;
import server.poptato.user.resolver.UserId;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 API.
     *
     * 사용자의 소셜 로그인 정보를 기반으로 인증을 처리하고, 새로운 유저일 경우 유저 데이터를 저장합니다.
     * 또한, FCM 토큰을 저장하거나 업데이트합니다.
     *
     * @param loginRequestDto 사용자의 로그인 요청 정보 (소셜 타입, 액세스 토큰, 클라이언트 ID 등)
     * @return 액세스 토큰, 리프레시 토큰, 유저 ID 및 신규 유저 여부를 포함한 응답 객체
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = authService.login(loginRequestDto);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    /**
     * 로그아웃 API.
     *
     * 현재 로그인한 유저를 로그아웃 처리합니다. 이 과정에서 해당 유저의 리프레시 토큰이 삭제됩니다.
     *
     * @param userId 로그아웃하려는 사용자의 ID (자동 주입됨)
     * @return 성공 여부를 나타내는 응답 객체
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<SuccessStatus>> logout(@UserId Long userId) {
        authService.logout(userId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    /**
     * 토큰 갱신 API.
     *
     * 만료되었거나 만료 예정인 액세스 토큰을 유효한 리프레시 토큰을 사용하여 갱신합니다.
     * 리프레시 토큰이 유효하지 않을 경우 예외가 발생합니다.
     *
     * @param reissueTokenRequestDto 토큰 갱신 요청 정보 (기존 리프레시 토큰)
     * @return 새로 발급된 액세스 토큰과 리프레시 토큰
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenPair>> refresh(@RequestBody ReissueTokenRequestDto reissueTokenRequestDto) {
        TokenPair response = authService.refresh(reissueTokenRequestDto);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }
}
