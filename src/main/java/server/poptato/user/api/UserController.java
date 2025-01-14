package server.poptato.user.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import server.poptato.global.response.BaseResponse;
import server.poptato.user.api.request.UserDeleteRequestDTO;
import server.poptato.user.application.response.UserInfoResponseDto;
import server.poptato.user.application.service.UserService;
import server.poptato.user.resolver.UserId;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/delete")
    public BaseResponse deleteUser(@UserId Long userId,
                                   @RequestBody UserDeleteRequestDTO requestDTO) {
        userService.deleteUser(userId, requestDTO);
        return new BaseResponse();
    }

    @GetMapping("/mypage")
    public BaseResponse getUserInfo(@UserId Long userId) {
        UserInfoResponseDto response = userService.getUserInfo(userId);
        return new BaseResponse(response);
    }
}