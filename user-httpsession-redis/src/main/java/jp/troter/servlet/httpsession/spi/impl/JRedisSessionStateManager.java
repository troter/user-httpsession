package jp.troter.servlet.httpsession.spi.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jp.troter.servlet.httpsession.spi.JRedisInitializer;
import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.spi.SessionValueSerializer;
import jp.troter.servlet.httpsession.state.DefaultSessionState;
import jp.troter.servlet.httpsession.state.SessionState;

import org.jredis.RedisException;
import org.jredis.ri.alphazero.support.DefaultCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JRedisSessionStateManager extends SessionStateManager {

    private static Logger log = LoggerFactory.getLogger(JRedisSessionStateManager.class);

    private static final String KEY_PREFIX = "httpsession";

    protected JRedisInitializer initializer;

    protected SessionValueSerializer serializer;

    @Override
    public SessionState loadState(String sessionId) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        long lastAccessedTime = new Date().getTime();
        int maxInactiveInterval = getDefaultTimeoutSecond();
        try {
            byte[] cellData = getInitializer().getJRedis().get(key(sessionId));
            if (cellData == null) { return new DefaultSessionState(maxInactiveInterval); }
            Cell cell = (Cell)DefaultCodec.decode(cellData);
            maxInactiveInterval = cell.getMaxInactiveInterval();
            lastAccessedTime = cell.getLastAccessedTime();
            if (lastAccessedTime > getTimeoutTime(maxInactiveInterval)) { return new DefaultSessionState(getDefaultTimeoutSecond()); }
            attributes.putAll(cell.getAttributes());
        } catch (RedisException e) {
            log.warn("Redis exception occurred. session_id=" + sessionId, e);
            removeState(sessionId);
        } catch (RuntimeException e) {
            log.warn("Redis exception occurred. session_id=" + sessionId, e);
            removeState(sessionId);
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
        try {
            getInitializer().getJRedis().set(key(sessionId), cell);
        } catch (RedisException e) {
            log.warn("Redis exception occurred. session_id=" + sessionId, e);
        } catch (RuntimeException e) {
            log.warn("Redis exception occurred. session_id=" + sessionId, e);
        }
    }

    @Override
    public void removeState(String sessionId) {
        try {
            getInitializer().getJRedis().del(key(sessionId));
        } catch (RedisException e) {
            log.warn("Redis exception occurred. session_id=" + sessionId, e);
        }
    }

    @Override
    public int getDefaultTimeoutSecond() {
        return getInitializer().getDefaultTimeoutSecond();
    }

    protected String key(String sessionId) {
        return KEY_PREFIX + "/" + sessionId;
    }

    protected JRedisInitializer getInitializer() {
        if (initializer == null) {
            initializer = JRedisInitializer.newInstance();
        }
        return initializer;
    }

    protected SessionValueSerializer getSerializer() {
        if (serializer == null) {
            serializer = SessionValueSerializer.newInstance();
        }
        return serializer;
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
