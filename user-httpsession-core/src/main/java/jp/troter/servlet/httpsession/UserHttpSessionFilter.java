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
import jp.troter.servlet.httpsession.wrapper.UserHttpSessionHttpServletRequestWrapper;
import jp.troter.servlet.httpsession.wrapper.UserHttpSessionHttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserHttpSessionFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(UserHttpSessionFilter.class);

    protected int retryLimit;
    public static final int DEFAULT_RETRY_LIMIT = 10;

    protected SessionStateManager sessionStateManager;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext context = filterConfig.getServletContext();
        if (context == null) {
            log.error("unable to init as servlet context is null");
            return;
        }
        ServletContextHolder.setInstance(new ServletContextHolder(filterConfig.getServletContext()));

        retryLimit = initParameterToInt(filterConfig, "retryLimit", DEFAULT_RETRY_LIMIT);
    }

    @Override
    public void destroy() { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        SessionStateManager ssm = getSessionStateManager();
        UserHttpSessionHttpServletRequestWrapper requestWrapper = newUserHttpSessionHttpServletRequestWrapper(
                (HttpServletRequest) request, (HttpServletResponse) response, ssm, retryLimit);
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
            SessionStateManager sessionStateManager, final int retryLimit
    ) {
        return new UserHttpSessionHttpServletRequestWrapper(request, response, sessionStateManager, retryLimit);
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

    protected int initParameterToInt(FilterConfig filterConfig, String parameterName, int defaultValue) {
        int value = defaultValue;
        String parameterStr = filterConfig.getInitParameter(parameterName);
        try {
            if (! StringUtils.isEmpty(parameterStr)) {
                value = Integer.parseInt(parameterStr);
            }
        } catch (NumberFormatException e) {
            log.debug(parameterName + " is not a number. use default value.", e);
        }
        return value;
    }
}
