package jp.troter.servlet.httpsession.spi.impl;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.state.DefaultSessionState;
import jp.troter.servlet.httpsession.state.SessionState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSessionStateManager extends SessionStateManager {

    private static Logger log = LoggerFactory.getLogger(DefaultSessionStateManager.class);

    private static final int DEFAULT_TIMEOUT_SECOND
        = Long.valueOf(TimeUnit.HOURS.toSeconds(1L)).intValue();

    Map<String, Cell> sessionStore = new ConcurrentHashMap<String, Cell>();

    @Override
    public int getDefaultTimeoutSecond() {
        String defaultTimeoutSecond = System.getProperty(PROPERTY_KEY_SESSION_STATE_DEFAULT_TIMEOUT_SECOND);
        if (defaultTimeoutSecond != null) {
            return Integer.valueOf(defaultTimeoutSecond).intValue();
        }
        return DEFAULT_TIMEOUT_SECOND;
    }

    @Override
    public boolean isThrowException() {
        String throwException = System.getProperty(PROPERTY_KEY_SESSION_STATE_THROW_EXCEPTION);
        if (throwException != null) {
            return Boolean.valueOf(throwException).booleanValue();
        }
        return false;
    }

    @Override
    public SessionState loadState(String sessionId) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        long lastAccessedTime = new Date().getTime();
        int maxInactiveInterval = getDefaultTimeoutSecond();

        try {
            Cell cell = sessionStore.get(sessionId);
            maxInactiveInterval = cell.getMaxInactiveInterval();
            lastAccessedTime = cell.getLastAccessedTime();
            if (lastAccessedTime > getTimeoutTime(maxInactiveInterval)) { newEmptySessionState(); }
            attributes.putAll(cell.getAttributes());
        } catch (RuntimeException e) {
            log.warn("RuntimeException occurred. session_id=" + sessionId, e);
            removeState(sessionId);
            if (isThrowException()) {
                throw e;
            }
        }

        return new DefaultSessionState(attributes, lastAccessedTime, false, maxInactiveInterval);
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
}
