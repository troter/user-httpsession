package jp.troter.servlet.httpsession.spi.impl;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.spi.SpyMemcachedInitializer;
import jp.troter.servlet.httpsession.state.DefaultSessionState;
import jp.troter.servlet.httpsession.state.SessionState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpyMemcachedSessionStateManager extends SessionStateManager {

    private static Logger log = LoggerFactory.getLogger(SpyMemcachedSessionStateManager.class);

    private static final String KEY_PREFIX = "httpsession";

    protected SpyMemcachedInitializer initializer;

    @Override
    public SessionState loadState(String sessionId) {
        try {
            Object obj = getInitializer().getMemcachedClient().get(key(sessionId));
            if (obj != null) {
                return restoredSessionState((Cell) obj);
            }
        } catch (RuntimeException e) {
            log.warn("Memcached exception occurred at get method. session_id=" + sessionId, e);
            removeState(sessionId);
        }

        return newEmptySessionState();
    }

    @Override
    public void saveState(String sessionId, SessionState sessionState) {
        Cell cell = storedSessionState(sessionState);
        try {
            getInitializer().getMemcachedClient().set(key(sessionId), cell.getMaxInactiveInterval(), cell);
        } catch (RuntimeException e) {
            log.warn("Memcached exception occurred at set method. session_id=" + sessionId, e);
        }
    }

    @Override
    public void removeState(String sessionId) {
        try {
            getInitializer().getMemcachedClient().delete(key(sessionId));
        } catch (RuntimeException e) {
            log.warn("Memcached exception occurred at delete method. session_id=" + sessionId, e);
        }
    }

    @Override
    public int getDefaultTimeoutSecond() {
        return getInitializer().getDefaultTimeoutSecond();
    }

    protected SessionState newEmptySessionState() {
        return new DefaultSessionState(getDefaultTimeoutSecond());
    }

    protected SessionState restoredSessionState(Cell cell) {
        int maxInactiveInterval = cell.getMaxInactiveInterval();
        long lastAccessedTime = cell.getLastAccessedTime();
        if (lastAccessedTime > getTimeoutTime(maxInactiveInterval)) {
            return newEmptySessionState();
        }
        return new DefaultSessionState(cell.getAttributes(), lastAccessedTime, false, maxInactiveInterval);
    }

    protected Cell storedSessionState(SessionState sessionState) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        for (Enumeration<?> e = sessionState.getAttributeNames(); e.hasMoreElements();) {
            String name = (String)e.nextElement();
            Object value = sessionState.getAttribute(name);
            if (value == null) { continue; }
            attributes.put(name, value);
        }
        return new Cell(attributes, sessionState.getCreationTime(), sessionState.getMaxInactiveInterval());
    }

    protected String key(String sessionId) {
        return KEY_PREFIX + "/" + sessionId;
    }

    protected SpyMemcachedInitializer getInitializer() {
        if (initializer == null) {
            initializer = SpyMemcachedInitializer.newInstance();
        }
        return initializer;
    }

    protected static class Cell implements Serializable {
        private static final long serialVersionUID = 1L;

        Map<String, Object> attributes;
        long lastAccessedTime;
        int maxInactiveInterval;

        public Cell(Map<String, Object> attributes, long lastAccessedTime, int maxInactiveInterval) {
            this.attributes = attributes;
            this.lastAccessedTime = lastAccessedTime;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        public long getLastAccessedTime() {
            return lastAccessedTime;
        }

        public int getMaxInactiveInterval() {
            return maxInactiveInterval;
        }
    }
}
