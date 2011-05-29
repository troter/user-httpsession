package jp.troter.servlet.httpsession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.troter.servlet.httpsession.spi.SessionCookieHandler;
import jp.troter.servlet.httpsession.spi.SessionStateManager;

import org.apache.commons.lang.StringUtils;

public class UserHttpSessionHttpServletRequestWrapper extends
        HttpServletRequestWrapper {

    protected final HttpServletRequest request;

    protected final HttpServletResponse response;

    protected final SessionStateManager sessionStateManager;

    protected final UserHttpSessionHolder sessionHolder;

    protected SessionCookieHandler sessionCookieHandler;

    protected String requestedSessionIdFromCookie;

    protected String requestedSessionIdFromURL;

    protected String generatedSessionId;

    public UserHttpSessionHttpServletRequestWrapper(HttpServletRequest request,
            HttpServletResponse response, SessionStateManager sessionStateManager) {
        super(request);
        this.request = request;
        this.response = response;
        this.sessionStateManager = sessionStateManager;
        sessionHolder = newUserHttpSessionHolder(this, sessionStateManager);
        setupSessionId(request);
    }

    /**
     * setup session id
     * @param request
     */
    protected void setupSessionId(HttpServletRequest request) {
        requestedSessionIdFromCookie = getSessionCookieHandler().getSessionIdFromCookie(request);
        if (requestedSessionIdFromCookie != null) {
            return;
        }
        requestedSessionIdFromURL = getSessionCookieHandler().getSessionIdFromURL(request);
    }

    @Override
    public String getRequestedSessionId() {
        if (requestedSessionIdFromCookie != null) {
            return requestedSessionIdFromCookie;
        }
        if (requestedSessionIdFromURL != null) {
            return requestedSessionIdFromURL;
        }
        return null;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public HttpSession getSession(boolean create) {
        HttpSession session = sessionHolder.getSession(create);
        if (session != null) {
            getSessionCookieHandler().writeCookie(request, response, session.getId());
        }
        return session;
    }

    public UserHttpSession getUserHttpSession() {
        return sessionHolder.getUserHttpSession();
    }

    /**
     * <p>invalidate session.</p>
     * call by {@code UserHttpSession.invalidate()}
     */
    public void invalidateSession(){
        String sessionId = getSession().getId();
        sessionHolder.sessionInvalidate();
        requestedSessionIdFromCookie = null;
        requestedSessionIdFromURL = null;
        if(StringUtils.isEmpty(sessionId)){
            getSessionCookieHandler().expireCookie(request, response, sessionId);
        }
    }

    public HttpServletRequest getOriginalRequest() {
        return request;
    }

    protected UserHttpSessionHolder newUserHttpSessionHolder(
            UserHttpSessionHttpServletRequestWrapper request, SessionStateManager sessionStateManager
    ) {
        return new UserHttpSessionHolder(request, sessionStateManager);
    }

    protected SessionCookieHandler getSessionCookieHandler() {
        if (sessionCookieHandler == null) {
            sessionCookieHandler = SessionCookieHandler.newInstance();
        }
        return sessionCookieHandler;
    }
}
