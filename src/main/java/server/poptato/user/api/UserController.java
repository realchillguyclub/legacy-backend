package server.poptato.user.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.poptato.auth.application.service.JwtService;
import server.poptato.global.response.ApiResponse;
import server.poptato.global.response.status.SuccessStatus;
import server.poptato.user.api.request.UserCommentRequestDTO;
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
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param userDeleteRequestDTO 탈퇴 요청 정보 (탈퇴 사유 등)
     * @return 성공 여부를 나타내는 응답
     */
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<SuccessStatus>> deleteUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UserDeleteRequestDTO userDeleteRequestDTO
    ) {
        userService.deleteUser(jwtService.extractUserIdFromToken(authorizationHeader), userDeleteRequestDTO);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    /**
     * 사용자 정보 조회 API.
     *
     * 사용자의 마이페이지 정보를 반환합니다.
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

    /**
     * 사용자 의견 생성 및 전송 API.
     *
     * 사용자가 일단에게 의견을 보내며, DB에 저장 및 해당 내용을 디스코드 & 노션에 전송합니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param requestDTO 의견 정보
     * @return 성공 여부를 나타내는 응답
     */
    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<SuccessStatus>> createAndSendUserComment(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UserCommentRequestDTO requestDTO
    ) {
        userService.createAndSendUserComment(jwtService.extractUserIdFromToken(authorizationHeader), requestDTO);
        return ApiResponse.onSuccess(SuccessStatus._CREATED);
    }
}
