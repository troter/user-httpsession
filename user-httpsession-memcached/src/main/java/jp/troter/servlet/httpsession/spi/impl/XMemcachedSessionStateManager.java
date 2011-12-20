package jp.troter.servlet.httpsession.spi.impl;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import jp.troter.servlet.httpsession.spi.XMemcachedInitializer;
import jp.troter.servlet.httpsession.state.SessionState;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMemcachedSessionStateManager extends DefaultSessionStateManager {

    private static Logger log = LoggerFactory.getLogger(XMemcachedSessionStateManager.class);

    protected XMemcachedInitializer initializer;

    @Override
    public SessionState loadState(String sessionId) {
        Throwable exception = null;
        try {
            Object obj = getInitializer().getMemcachedClient().get(key(sessionId));
            if (obj != null) {
                return restoredSessionState((Cell) obj);
            }
        } catch (InterruptedException e) {
            log.warn("Interrupt occurred at get method. session_id=" + sessionId, e);
            exception = e;
        } catch (MemcachedException e) {
            log.warn("Memcached exception occurred at get method. session_id=" + sessionId, e);
            exception = e;
        } catch (TimeoutException e) {
            log.warn("Timeout occurred at get method. session_id=" + sessionId, e);
            exception = e;
        }
        if (exception != null) {
            removeState(sessionId);
            if (isThrowException()) {
                throw new RuntimeException(exception);
            }
        }

        return newEmptySessionState();
    }

    @Override
    public void saveState(String sessionId, SessionState sessionState) {
        Cell cell = storedSessionState(sessionState);
        Throwable exception = null;
        try {
            getInitializer().getMemcachedClient().set(key(sessionId), cell.getMaxInactiveInterval(), cell);
        } catch (InterruptedException e) {
            log.warn("Interrupt occurred at set method. session_id=" + sessionId, e);
            exception = e;
        } catch (MemcachedException e) {
            log.warn("Memcached exception occurred at set method. session_id=" + sessionId, e);
            exception = e;
        } catch (TimeoutException e) {
            log.warn("Timeout occurred at set method. session_id=" + sessionId, e);
            exception = e;
        }
        if (exception != null) {
            if (isThrowException()) {
                throw new RuntimeException(exception);
            }
        }
    }

    @Override
    public void removeState(String sessionId) {
        Throwable exception = null;
        try {
            getInitializer().getMemcachedClient().delete(key(sessionId));
        } catch (InterruptedException e) {
            log.warn("Interrupt occurred at delete method. session_id=" + sessionId, e);
            exception = e;
        } catch (MemcachedException e) {
            log.warn("Memcached exception occurred at delete method. session_id=" + sessionId, e);
            exception = e;
        } catch (TimeoutException e) {
            log.warn("Timeout occurred at delete method. session_id=" + sessionId, e);
            exception = e;
        }
        if (exception != null) {
            if (isThrowException()) {
                throw new RuntimeException(exception);
            }
        }
    }

    protected SessionState restoredSessionState(Cell cell) {
        int maxInactiveInterval = cell.getMaxInactiveInterval();
        long lastAccessedTime = cell.getLastAccessedTime();
        if (lastAccessedTime > getTimeoutTime(maxInactiveInterval)) {
            return newEmptySessionState();
        }
        return newSessionState(cell.getAttributes(), lastAccessedTime, false, maxInactiveInterval);
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

    protected XMemcachedInitializer getInitializer() {
        if (initializer == null) {
            initializer = XMemcachedInitializer.newInstance();
        }
        return initializer;
    }
}
