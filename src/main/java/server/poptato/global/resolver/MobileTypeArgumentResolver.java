package server.poptato.global.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import server.poptato.global.exception.CustomException;
import server.poptato.global.response.status.ErrorStatus;
import server.poptato.user.domain.value.MobileType;

public class MobileTypeArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == MobileType.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String header = webRequest.getHeader("X-Mobile-Type");

        if (header == null) {
            throw new CustomException(ErrorStatus._INVALID_HEADER_VALUE);
        }

        try {
            return MobileType.valueOf(header.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorStatus._INVALID_HEADER_VALUE);
        }
    }
}