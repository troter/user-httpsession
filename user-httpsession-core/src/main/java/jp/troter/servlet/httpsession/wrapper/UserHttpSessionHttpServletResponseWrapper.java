package jp.troter.servlet.httpsession.wrapper;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import jp.troter.servlet.httpsession.spi.SessionCookieHandler;
import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.state.SessionState;

public class UserHttpSessionHttpServletResponseWrapper extends
        HttpServletResponseWrapper {

    protected final HttpServletResponse response;

    protected final UserHttpSessionHttpServletRequestWrapper requestWrapper;

    protected final SessionStateManager sessionStateManager;

    protected SessionCookieHandler sessionCookieHandler;

    public UserHttpSessionHttpServletResponseWrapper(HttpServletResponse response,
            UserHttpSessionHttpServletRequestWrapper request, SessionStateManager sessionStateManager) {
        super(response);
        this.response = response;
        this.requestWrapper = request;
        this.sessionStateManager = sessionStateManager;
    }

    @Deprecated
    @Override
    public String encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }

    @Override
    public String encodeRedirectURL(String url) {
        return getSessionCookieHandler().rewriteURL(url, requestWrapper);
    }

    @Deprecated
    @Override
    public String encodeUrl(String url) {
        return super.encodeURL(url);
    }

    @Override
    public String encodeURL(String url) {
        return getSessionCookieHandler().rewriteURL(url, requestWrapper);
    }

    @Override
    public void flushBuffer() throws IOException {
        saveState(sessionStateManager);
        super.flushBuffer();
    }

    public void saveState(SessionStateManager sessionStateManager) {
        UserHttpSession session = requestWrapper.getUserHttpSession();
        if (session != null) {
            SessionState sessionState = session.getSessionState();
            if (sessionState != null) {
                sessionStateManager.saveState(session.getId(), sessionState);
            }
        }
    }

    protected SessionCookieHandler getSessionCookieHandler() {
        if (sessionCookieHandler == null) {
            sessionCookieHandler = SessionCookieHandler.newInstance();
        }
        return sessionCookieHandler;
    }
}
