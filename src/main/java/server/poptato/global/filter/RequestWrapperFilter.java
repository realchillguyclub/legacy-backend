package server.poptato.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import server.poptato.global.wrapper.CustomHttpRequestWrapper;

import java.io.IOException;

@Component
public class RequestWrapperFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            CustomHttpRequestWrapper wrapper = new CustomHttpRequestWrapper(httpRequest);
            chain.doFilter(wrapper, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
