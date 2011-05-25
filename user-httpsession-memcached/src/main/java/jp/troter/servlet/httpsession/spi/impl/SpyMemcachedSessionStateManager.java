package jp.troter.servlet.httpsession.spi.impl;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.spi.SessionValueSerializer;
import jp.troter.servlet.httpsession.spi.SpyMemcachedInitializer;
import jp.troter.servlet.httpsession.state.DefaultSessionState;
import jp.troter.servlet.httpsession.state.SessionState;

public class SpyMemcachedSessionStateManager extends SessionStateManager {

    public static final String KEY_PREFIX = "httpsession";

    protected SpyMemcachedInitializer initializer;

    protected SessionValueSerializer serializer;

    @SuppressWarnings("unchecked")
    @Override
    public SessionState loadState(String sessionId) {
        Object obj = getSpyMemcachedInitializer().getMemcachedClient().get(key(sessionId));
        Map<String, Object> attributes = new HashMap<String, Object>();
        if (obj != null) {

            Map<String, Object> rawAttributes = (Map<String, Object>) obj;
            for (String key : rawAttributes.keySet()) {
                try {
                    byte[] objectData = (byte[])rawAttributes.get(key);
                    attributes.put(key, getSessionValueSerializer().deserialize(objectData));
                } catch (Exception UserHttpSessionSerializationException) {
                }
            }
        }

        return new DefaultSessionState(attributes);
    }

    @Override
    public void updateState(String sessionId, SessionState sessionState) {
        Map<String, Object> attributes = new HashMap<String, Object>();

        for (Enumeration<?> e = sessionState.getAttributeNames(); e.hasMoreElements();) {
            String name = (String)e.nextElement();
            Object value = sessionState.getAttribute(name);
            if (value == null) { continue; }
            try {
                attributes.put(name, getSessionValueSerializer().serialize((Serializable)value));
            } catch (Exception UserHttpSessionSerializationException) {
            }
        }

        getSpyMemcachedInitializer().getMemcachedClient().set(key(sessionId), initializer.getSessionTimeout(), attributes);
    }

    @Override
    public void removeState(String sessionId) {
        getSpyMemcachedInitializer().getMemcachedClient().delete(key(sessionId));
    }

    protected String key(String sessionId) {
        return KEY_PREFIX + "/" + sessionId;
    }

    protected SpyMemcachedInitializer getSpyMemcachedInitializer() {
        if (initializer == null) {
            initializer = SpyMemcachedInitializer.newInstance();
        }
        return initializer;
    }

    protected SessionValueSerializer getSessionValueSerializer() {
        if (serializer == null) {
            serializer = SessionValueSerializer.newInstance();
        }
        return serializer;
    }
}
