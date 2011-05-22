package jp.troter.servlet.httpsession;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.troter.servlet.httpsession.spi.SessionStateManager;

public class UserHttpSessionFilter implements Filter {

    protected SessionStateManager sessionStateManager;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        SessionStateManager ssm = getSessionStateManager();
        UserHttpSessionHttpServletRequestWrapper requestWrapper = new UserHttpSessionHttpServletRequestWrapper(
                (HttpServletRequest) request, (HttpServletResponse) response, ssm);
        UserHttpSessionHttpServletResponseWrapper responseWrapper = new UserHttpSessionHttpServletResponseWrapper(
                (HttpServletResponse) response, requestWrapper, ssm);

        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            responseWrapper.updateState(ssm);
        }
    }

    protected SessionStateManager getSessionStateManager() {
        if (sessionStateManager == null) {
            sessionStateManager = SessionStateManager.getInstance();
        }
        return sessionStateManager;
    }
}
