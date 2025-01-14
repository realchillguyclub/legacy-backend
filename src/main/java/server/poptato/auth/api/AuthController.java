package server.poptato.auth.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.poptato.auth.api.request.LoginRequestDto;
import server.poptato.auth.api.request.ReissueTokenRequestDto;
import server.poptato.auth.application.response.LoginResponseDto;
import server.poptato.auth.application.service.AuthService;
import server.poptato.global.dto.TokenPair;
import server.poptato.global.response.BaseResponse;
import server.poptato.user.resolver.UserId;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public BaseResponse<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = authService.login(loginRequestDto);
        return new BaseResponse<>(response);
    }

    @PostMapping("/logout")
    public BaseResponse logout(@UserId Long userId) {
        authService.logout(userId);
        return new BaseResponse();
    }

    @PostMapping("/refresh")
    public BaseResponse<TokenPair> refresh(@RequestBody final ReissueTokenRequestDto reissueTokenRequestDto) {
        TokenPair response = authService.refresh(reissueTokenRequestDto);
        return new BaseResponse<>(response);
    }
}
