package server.poptato.user.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import server.poptato.auth.application.service.JwtService;
import server.poptato.auth.status.AuthErrorStatus;
import server.poptato.global.exception.CustomException;

@Component
@RequiredArgsConstructor
public class UserResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;

    /**
     * 주어진 메서드 파라미터가 @UserId 어노테이션을 가지고 있고,
     * 타입이 Long일 경우 이 Resolver가 처리하도록 설정.
     *
     * @param parameter 현재 처리 중인 메서드의 파라미터 정보
     * @return 해당 파라미터를 처리할 수 있는지 여부
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserId.class) && Long.class.equals(parameter.getParameterType());
    }

    /**
     * 메서드 파라미터를 실제 값으로 변환하는 로직.
     * Authorization 헤더에서 JWT 토큰을 추출하고, 이를 검증하여 유저 ID를 반환.
     *
     * @param parameter    현재 처리 중인 메서드의 파라미터 정보
     * @param mavContainer ModelAndViewContainer (사용하지 않음)
     * @param webRequest   현재 요청 정보
     * @param binderFactory 데이터 바인딩 팩토리 (사용하지 않음)
     * @return Long 타입의 유저 ID
     * @throws CustomException 토큰이 없거나 유효하지 않을 경우 예외 발생
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        // Authorization 헤더에서 토큰 추출
        final String token = request.getHeader("Authorization");
        if (token == null || token.isBlank() || !token.startsWith("Bearer ")) {
            throw new CustomException(AuthErrorStatus._TOKEN_NOT_EXIST); // 토큰이 존재하지 않거나 형식이 잘못된 경우 예외 발생
        }

        // Bearer 문자열 제거 후 실제 토큰 값 추출
        final String extractedToken = token.substring("Bearer ".length());

        // JWT 토큰 검증
        jwtService.verifyToken(extractedToken);

        // 토큰에서 유저 ID 추출
        final String decodedUserId = jwtService.getUserIdInToken(extractedToken);
        try {
            // 유저 ID를 Long 타입으로 변환하여 반환
            return Long.parseLong(decodedUserId);
        } catch (NumberFormatException e) {
            // 유저 ID가 숫자로 변환할 수 없는 경우 예외 발생
            throw new CustomException(AuthErrorStatus._INVALID_TOKEN);
        }
    }
}
