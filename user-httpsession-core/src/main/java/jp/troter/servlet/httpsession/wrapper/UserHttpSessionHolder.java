package jp.troter.servlet.httpsession.wrapper;

import jp.troter.servlet.httpsession.spi.SessionIdGenerator;
import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.spi.SessionValidator;


public class UserHttpSessionHolder {

    protected UserHttpSessionHttpServletRequestWrapper request;

    protected SessionStateManager sessionStateManager;

    protected UserHttpSession session;

    protected SessionValidator sessionValidator;

    protected SessionIdGenerator sessionIdGenerator;

    public UserHttpSessionHolder(UserHttpSessionHttpServletRequestWrapper request, SessionStateManager sessionStateManager) {
        this.request = request;
        this.sessionStateManager = sessionStateManager;
    }

    public UserHttpSession getUserHttpSession() {
        return session;
    }

    public UserHttpSession getSession(boolean create) {
        // session already exists.
        if (session != null) { return session; }

        // session not exists, and not create
        if (!create) { return null; }

        boolean needNewSession = false;
        String sessionId = request.getRequestedSessionId();
        if (sessionId == null) {
            needNewSession = true;
            sessionId = getSessionIdGenerator().generateSessionId(request);
        }

        session = createHttpSession(sessionId, needNewSession);
        return session;
    }

    protected UserHttpSession createHttpSession(final String sessionId,
            final boolean needNewSession) {
        String currentSessionId = sessionId;
        boolean isNew = needNewSession;
        int retry = 0;
        while (true) {
            if (retry > getSessionIdGenerator().getRetryLimit()) {
                throw new RuntimeException("cannot create session.");
            }
            UserHttpSession session = newUserHttpSession(request, currentSessionId, sessionStateManager);

            boolean isConflictSessionId = isNew && getSessionValidator().isExistsMarker(session);
            if (! isConflictSessionId) {
                getSessionValidator().setupMarker(session, request);
                if (getSessionValidator().isValid(session, request)) {
                    // session is valid. return.
                    return session;
                }
            }

            session.invalidate();
            currentSessionId = getSessionIdGenerator().generateSessionId(request, retry);
            isNew = true;
            retry++;
        }
    }

    protected UserHttpSession newUserHttpSession(UserHttpSessionHttpServletRequestWrapper request, String id,
            SessionStateManager sessionStateManager) {
        return new UserHttpSession(request, id, sessionStateManager);
    }

    /**
     * invalidate holding session.
     */
    public void sessionInvalidate() {
        session = null;
    }

    public boolean invalidate() {
        return session == null;
    }

    protected SessionValidator getSessionValidator() {
        if (sessionValidator == null) {
            sessionValidator = SessionValidator.newInstance();
        }
        return sessionValidator;
    }

    protected SessionIdGenerator getSessionIdGenerator() {
        if (sessionIdGenerator == null) {
            sessionIdGenerator = SessionIdGenerator.newInstance();
        }
        return sessionIdGenerator;
    }
}
