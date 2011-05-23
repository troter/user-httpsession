package jp.troter.servlet.httpsession.state;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

public class DefaultSessionState implements SessionState {

    Map<String, Object> map;

    protected long lastAccessedTime;

    public DefaultSessionState(Map<String, Object> map) {
        this(map, 0);
    }

    public DefaultSessionState(Map<String, Object> map, long lastAccessedTime) {
        this.map = map;
        this.lastAccessedTime = lastAccessedTime;
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

}
