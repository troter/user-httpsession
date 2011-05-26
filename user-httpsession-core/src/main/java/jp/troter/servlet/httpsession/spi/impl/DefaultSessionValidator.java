package jp.troter.servlet.httpsession.spi.impl;

import jp.troter.servlet.httpsession.UserHttpSession;
import jp.troter.servlet.httpsession.UserHttpSessionHttpServletRequestWrapper;
import jp.troter.servlet.httpsession.spi.SessionValidator;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSessionValidator extends SessionValidator {

    private static Logger log = LoggerFactory.getLogger(DefaultSessionValidator.class);

    protected static final String HTTPSESSION_REMOTE_ADDR
        = "jp.troter.servlet.httpsession.spi.impl.DefaultSessionValidator.REMOTE_ADDR";
    protected static final String HTTPSESSION_USER_AGENT
        = "jp.troter.servlet.httpsession.spi.impl.DefaultSessionValidator.USER_AGENT";

    protected String getMakerNameRemoteAddr() {
        return HTTPSESSION_REMOTE_ADDR;
    }

    protected String getMakerNameUserAgent() {
        return HTTPSESSION_USER_AGENT;
    }

    @Override
    public boolean isExistsMarker(UserHttpSession session) {
        return session.getAttribute(getMakerNameRemoteAddr()) != null
            || session.getAttribute(getMakerNameUserAgent()) != null;
    }

    @Override
    public void setupMarker(UserHttpSession session, UserHttpSessionHttpServletRequestWrapper request) {
        if (session.getAttribute(getMakerNameRemoteAddr()) == null) {
            session.setAttribute(getMakerNameRemoteAddr(),
                    ObjectUtils.toString(request.getRemoteAddr(), ""));
        }
        if (session.getAttribute(getMakerNameUserAgent()) == null) {
            session.setAttribute(getMakerNameUserAgent(),
                    ObjectUtils.toString(request.getHeader("User-Agent"), ""));
        }
    }

    @Override
    public boolean isValid(UserHttpSession session, UserHttpSessionHttpServletRequestWrapper request) {
        String requestRemoteAddr = ObjectUtils.toString(request.getRemoteAddr(), "");
        String requestUserAgent  = ObjectUtils.toString(request.getHeader("User-Agent"), "");
        String sessionRemoteAddr = ObjectUtils.toString(session.getAttribute(getMakerNameRemoteAddr()), "");
        String sessionUserAgent  = ObjectUtils.toString(session.getAttribute(getMakerNameUserAgent()), "");

        boolean hasPossibilityOfSessionHijack
            =  ! StringUtils.equals(sessionRemoteAddr, requestRemoteAddr)
            || ! StringUtils.equals(sessionUserAgent, requestUserAgent);
        if (hasPossibilityOfSessionHijack) {
            String message = getHijackSessionMessage(
                    request.getRequestURI(), session.getId(),
                    requestRemoteAddr, requestUserAgent,
                    sessionRemoteAddr, sessionUserAgent);
            log.warn(message);
        }

        return ! hasPossibilityOfSessionHijack;
    }

    protected String getHijackSessionMessage(
            String requestUrl, String sessionId,
            String requestRemoteAddr, String requestUserAgent,
            String sessionRemoteAddr, String sessionUserAgent)
    {
        StringBuilder m = new StringBuilder();
        m.append("Session hijack occured!");
        m.append("[");
        m.append("request_url=").append(requestUrl);
        m.append(", ");
        m.append("session_id=").append(sessionId);
        m.append(", ");
        m.append("request_remote_addr=").append(requestRemoteAddr);
        m.append(", ");
        m.append("request_user_agent=").append(requestUserAgent);
        m.append(", ");
        m.append("session_remote_addr=").append(sessionRemoteAddr);
        m.append(", ");
        m.append("session_user_agent=").append(sessionUserAgent);
        m.append("]");
        return m.toString();
    }
}
