package jp.troter.servlet.httpsession;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.troter.servlet.httpsession.spi.SessionStateManager;

public class UserHttpSessionFilter implements Filter {

    protected FilterConfig filterConfig;
    protected SessionStateManager sessionStateManager;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        SessionStateManager ssm = getSessionStateManager();
        UserHttpSessionHttpServletRequestWrapper requestWrapper = newUserHttpSessionHttpServletRequestWrapper(
                (HttpServletRequest) request, (HttpServletResponse) response, filterConfig.getServletContext(), ssm);
        UserHttpSessionHttpServletResponseWrapper responseWrapper = newUserHttpSessionHttpServletResponseWrapper(
                (HttpServletResponse) response, requestWrapper, ssm);

        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            responseWrapper.saveState(ssm);
        }
    }

    protected UserHttpSessionHttpServletRequestWrapper newUserHttpSessionHttpServletRequestWrapper(
            HttpServletRequest request, HttpServletResponse response,
            ServletContext servletContext,
            SessionStateManager sessionStateManager
    ) {
        return new UserHttpSessionHttpServletRequestWrapper(request, response, servletContext, sessionStateManager);
    }

    protected UserHttpSessionHttpServletResponseWrapper newUserHttpSessionHttpServletResponseWrapper(
            HttpServletResponse response, UserHttpSessionHttpServletRequestWrapper requestWrapper,
            SessionStateManager sessionStateManager
    ) {
        return new UserHttpSessionHttpServletResponseWrapper(response, requestWrapper, sessionStateManager);
    }

    protected SessionStateManager getSessionStateManager() {
        if (sessionStateManager == null) {
            sessionStateManager = SessionStateManager.getInstance();
        }
        return sessionStateManager;
    }
}
