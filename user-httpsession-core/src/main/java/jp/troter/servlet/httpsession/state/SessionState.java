package jp.troter.servlet.httpsession.state;

import java.util.Enumeration;

/**
 * session state
 */
public interface SessionState {

    long getLastAccessedTime();

    Enumeration<?> getAttributeNames();

    void setAttribute(String name, Object value);

    Object getAttribute(String name);
}
