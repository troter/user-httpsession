package jp.troter.servlet.httpsession.spi.memcached;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jp.troter.servlet.httpsession.spi.memcached.initializer.MemcachedJavaClientInitializer;
import jp.troter.servlet.httpsession.spi.support.DefaultSessionStateManager;
import jp.troter.servlet.httpsession.state.DefaultSessionState;
import jp.troter.servlet.httpsession.state.SessionState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemcachedJavaClientSessionStateManager extends DefaultSessionStateManager {

    private static Logger log = LoggerFactory.getLogger(MemcachedJavaClientSessionStateManager.class);

    protected MemcachedJavaClientInitializer initializer;

    @Override
    public SessionState loadState(String sessionId) {
        try {
            Object obj = getInitializer().getMemCachedClient().get(key(sessionId));
            if (obj != null) {
                return restoredSessionState((Cell) obj);
            }
        } catch (RuntimeException e) {
            log.warn("Memcached exception occurred at get method. session_id=" + sessionId, e);
            removeState(sessionId);
            if (isThrowException()) {
                throw e;
            }
        }

        return newEmptySessionState();
    }

    @Override
    public void saveState(String sessionId, SessionState sessionState) {
        Cell cell = storedSessionState(sessionState);
        try {
            getInitializer().getMemCachedClient().set(key(sessionId), cell, new Date(getTimeoutTime(cell.getMaxInactiveInterval())));
        } catch (RuntimeException e) {
            log.warn("Memcached exception occurred at set method. session_id=" + sessionId, e);
            if (isThrowException()) {
                throw e;
            }
        }
    }

    @Override
    public void removeState(String sessionId) {
        try {
            getInitializer().getMemCachedClient().delete(key(sessionId));
        } catch (RuntimeException e) {
            log.warn("Memcached exception occurred at delete method. session_id=" + sessionId, e);
            if (isThrowException()) {
                throw e;
            }
        }
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
        return getNameSpace() + "/" + sessionId;
    }

    protected MemcachedJavaClientInitializer getInitializer() {
        if (initializer == null) {
            initializer = MemcachedJavaClientInitializer.newInstance();
        }
        return initializer;
    }
}
