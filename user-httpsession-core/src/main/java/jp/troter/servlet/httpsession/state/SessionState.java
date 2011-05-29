package jp.troter.servlet.httpsession.state;

import java.util.Enumeration;

/**
 * session state
 */
public interface SessionState {

    long getCreationTime();

    long getLastAccessedTime();

    boolean isNew();

    Enumeration<?> getAttributeNames();

    void setAttribute(String name, Object value);

    Object getAttribute(String name);

    int getMaxInactiveInterval();

    void setMaxInactiveInterval(int interval);
}
