package jp.troter.servlet.httpsession.state;

import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSessionState implements SessionState {

    Map<String, Object> attributes;

    protected final long creationTime = new Date().getTime();

    protected long lastAccessedTime;

    protected boolean isNew;

    protected int maxInactiveInterval;

    public DefaultSessionState(int maxInactiveInterval) {
        this(Collections.<String, Object>emptyMap(), new Date().getTime(), true, maxInactiveInterval);
    }

    public DefaultSessionState(Map<String, Object> attributes, long lastAccessedTime, boolean isNew, int maxInactiveInterval) {
        this.attributes = new ConcurrentHashMap<String, Object>(attributes);
        this.lastAccessedTime = lastAccessedTime;
        this.isNew = isNew;
        this.maxInactiveInterval = maxInactiveInterval;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public Enumeration<?> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        maxInactiveInterval = interval;
    }
}
