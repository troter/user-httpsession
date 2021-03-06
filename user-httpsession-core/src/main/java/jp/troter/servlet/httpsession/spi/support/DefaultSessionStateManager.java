package jp.troter.servlet.httpsession.spi.support;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.state.DefaultSessionState;
import jp.troter.servlet.httpsession.state.SessionState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSessionStateManager extends SessionStateManager {

    private static Logger log = LoggerFactory.getLogger(DefaultSessionStateManager.class);

    Map<String, Cell> sessionStore = new ConcurrentHashMap<String, Cell>();

    @Override
    public SessionState loadState(String sessionId) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        long lastAccessedTime = new Date().getTime();
        int maxInactiveInterval = getDefaultTimeoutSecond();

        try {
            Cell cell = sessionStore.get(sessionId);
            if (cell == null) {
                return newEmptySessionState();
            }
            maxInactiveInterval = cell.getMaxInactiveInterval();
            lastAccessedTime = cell.getLastAccessedTime();
            if (lastAccessedTime > getTimeoutTime(maxInactiveInterval)) {
                return newEmptySessionState();
            }
            attributes.putAll(cell.getAttributes());
        } catch (RuntimeException e) {
            log.warn("RuntimeException occurred. session_id=" + sessionId, e);
            removeState(sessionId);
            if (isThrowException()) {
                throw e;
            }
        }

        return newSessionState(attributes, lastAccessedTime, false, maxInactiveInterval);
    }

    @Override
    public void saveState(String sessionId, SessionState sessionState) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        for (Enumeration<?> e = sessionState.getAttributeNames(); e.hasMoreElements();) {
            String name = (String)e.nextElement();
            Object value = sessionState.getAttribute(name);
            if (value == null) { continue; }
            attributes.put(name, value);
        }

        Cell cell = new Cell(attributes, sessionState.getCreationTime(), sessionState.getMaxInactiveInterval());
        sessionStore.put(sessionId, cell);
    }

    @Override
    public void removeState(String sessionId) {
        sessionStore.remove(sessionId);
    }

    protected SessionState newEmptySessionState() {
        return new DefaultSessionState(getDefaultTimeoutSecond());
    }

    protected SessionState newSessionState(Map<String, Object> attributes, long lastAccessedTime, boolean isNew, int maxInactiveInterval) {
        return new DefaultSessionState(attributes, lastAccessedTime, isNew, maxInactiveInterval);
    }
}
