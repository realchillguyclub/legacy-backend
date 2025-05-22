package server.poptato.global.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import server.poptato.global.wrapper.CustomHttpRequestWrapper;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";

    /**
     * 요청 전 처리 로직을 수행합니다.
     * - URI 경로 변수, 쿼리 파라미터, 요청 본문(body)을 로그로 출력합니다.
     * - 요청 시작 시간(startTime)을 request attribute에 저장합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param handler  핸들러 객체 (Controller 메서드)
     * @return true일 경우 요청을 계속 진행
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            Map<String, String> pathVariables =
                    (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (pathVariables != null && !pathVariables.isEmpty()) {
                log.info("📦 [{}] {} \npathVars : {}", request.getMethod(), request.getRequestURI(), pathVariables);
            }
        }

        if (request.getParameterNames().hasMoreElements()) {
            log.info("📦️ [{}] {} \nqueryParams : {}", request.getMethod(), request.getRequestURI(), getRequestParams(request));
        }

        if (request instanceof CustomHttpRequestWrapper wrapper) {
            String body = new String(wrapper.getRequestBody());
            if (!body.isBlank()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Object json = mapper.readValue(body, Object.class);
                    String prettyBody = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
                    log.info("📦 [{}] {} \nbody : {}", request.getMethod(), request.getRequestURI(), prettyBody);
                } catch (Exception e) {
                    log.info("📦 [{}] {} \nbody(raw) : {}", request.getMethod(), request.getRequestURI(), body);
                }
            }
        }

        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    /**
     * 요청 완료 후 처리 로직을 수행합니다.
     * - 요청 처리 시간과 HTTP 상태 코드를 포함한 로그를 출력합니다.
     * - 에러 상태(4xx, 5xx)는 ❌ 로그로, 정상 응답은 ✅ 로그로 구분합니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param handler  핸들러 객체 (Controller 메서드)
     * @param ex       처리 중 발생한 예외 (없을 경우 null)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long start = (Long) request.getAttribute(START_TIME);
        long duration = System.currentTimeMillis() - (start != null ? start : 0L);

        int status = response.getStatus();

        if (status >= 400) {
            log.error("❌ [{}] {} request failed - {}ms | status={}", request.getMethod(), request.getRequestURI(), duration, status);
        } else {
            log.info("✅ [{}] {} request completed - {}ms | status={}", request.getMethod(), request.getRequestURI(), duration, status);
        }
    }

    /**
     * 요청 파라미터를 key-value 형태로 추출하여 Map으로 반환합니다.
     *
     * @param request HTTP 요청 객체
     * @return 요청 파라미터 Map
     */
    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            paramMap.put(paramName, request.getParameter(paramName));
        }

        return paramMap;
    }
}
