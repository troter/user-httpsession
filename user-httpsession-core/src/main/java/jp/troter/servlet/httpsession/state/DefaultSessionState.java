package jp.troter.servlet.httpsession.state;

import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSessionState implements SessionState {

    Map<String, Object> map;

    protected final long creationTime = new Date().getTime();

    protected long lastAccessedTime;

    protected boolean isNew;

    public DefaultSessionState() {
        this(Collections.<String, Object>emptyMap(), new Date().getTime(), true);
    }

    public DefaultSessionState(Map<String, Object> map, long lastAccessedTime, boolean isNew) {
        this.map = new ConcurrentHashMap<String, Object>(map);
        this.lastAccessedTime = lastAccessedTime;
        this.isNew = isNew;
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
        return Collections.enumeration(map.keySet());
    }

    @Override
    public void setAttribute(String name, Object value) {
        map.put(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        return map.get(name);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

}
