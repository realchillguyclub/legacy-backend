package server.poptato.user.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.poptato.global.response.ApiResponse;
import server.poptato.global.response.status.SuccessStatus;
import server.poptato.user.api.request.UserDeleteRequestDTO;
import server.poptato.user.application.response.UserInfoResponseDto;
import server.poptato.user.application.service.UserService;
import server.poptato.user.resolver.UserId;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * 사용자 탈퇴 API.
     *
     * 사용자가 요청한 계정 탈퇴를 처리합니다.
     * 추가 인증 정보 또는 탈퇴 사유를 요청 본문으로 전달받습니다.
     *
     * @param userId 사용자 ID (자동 주입)
     * @param requestDTO 탈퇴 요청 정보 (탈퇴 사유 등)
     * @return 성공 여부를 나타내는 응답
     */
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<SuccessStatus>> deleteUser(
            @UserId Long userId,
            @RequestBody UserDeleteRequestDTO requestDTO
    ) {
        userService.deleteUser(userId, requestDTO);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    /**
     * 사용자 정보 조회 API.
     *
     * 사용자의 마이페이지 정보를 반환합니다.
     * 사용자 이름, 이메일, 등록된 설정 등의 정보를 포함합니다.
     *
     * @param userId 사용자 ID (자동 주입)
     * @return 사용자 정보 DTO
     */
    @GetMapping("/mypage")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> getUserInfo(
            @UserId Long userId
    ) {
        UserInfoResponseDto response = userService.getUserInfo(userId);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }
}
