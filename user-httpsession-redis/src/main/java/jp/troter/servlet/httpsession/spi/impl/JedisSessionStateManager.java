package jp.troter.servlet.httpsession.spi.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jp.troter.servlet.httpsession.spi.JedisInitializer;
import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.spi.SessionValueSerializer;
import jp.troter.servlet.httpsession.state.DefaultSessionState;
import jp.troter.servlet.httpsession.state.SessionState;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class JedisSessionStateManager extends SessionStateManager {

    private static Logger log = LoggerFactory.getLogger(JedisSessionStateManager.class);

    private static final String KEY_PREFIX = "httpsession";

    protected JedisInitializer initializer;

    protected SessionValueSerializer serializer;

    @Override
    public SessionState loadState(String sessionId) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        long lastAccessedTime = new Date().getTime();
        int maxInactiveInterval = getDefaultTimeoutSecond();

        Jedis jedis = getInitializer().getJedisPool().getResource();
        try {
            String hexCellData = jedis.get(key(sessionId));
            if (hexCellData == null) { return newEmptySessionState(); }
            Cell cell = (Cell)getSerializer().deserialize(Hex.decodeHex(hexCellData.toCharArray()));
            maxInactiveInterval = cell.getMaxInactiveInterval();
            lastAccessedTime = cell.getLastAccessedTime();
            if (lastAccessedTime > getTimeoutTime(maxInactiveInterval)) { newEmptySessionState(); }
            attributes.putAll(cell.getAttributes());
        } catch (RuntimeException e) {
            log.warn("Redis exception occurred. session_id=" + sessionId, e);
            removeState(sessionId);
        } catch (DecoderException e) {
            log.warn("Redis exception occurred. session_id=" + sessionId, e);
            removeState(sessionId);
        } finally {
            getInitializer().getJedisPool().returnResource(jedis);
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
        Jedis jedis = getInitializer().getJedisPool().getResource();
        try {
            byte[] cellData = getSerializer().serialize(cell);
            jedis.set(key(sessionId), Hex.encodeHexString(cellData));
        } catch (RuntimeException e) {
            log.warn("Redis exception occurred. session_id=" + sessionId, e);
        } finally {
            getInitializer().getJedisPool().returnResource(jedis);
        }
    }

    @Override
    public void removeState(String sessionId) {
        Jedis jedis = getInitializer().getJedisPool().getResource();
        try {
            jedis.del(key(sessionId));
        } catch (RuntimeException e) {
            log.warn("Redis exception occurred. session_id=" + sessionId, e);
        } finally {
            getInitializer().getJedisPool().returnResource(jedis);
        }
    }

    @Override
    public int getDefaultTimeoutSecond() {
        return getInitializer().getDefaultTimeoutSecond();
    }

    protected SessionState newEmptySessionState() {
        return new DefaultSessionState(getDefaultTimeoutSecond());
    }


    protected String key(String sessionId) {
        return KEY_PREFIX + "/" + sessionId;
    }

    protected JedisInitializer getInitializer() {
        if (initializer == null) {
            initializer = JedisInitializer.newInstance();
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