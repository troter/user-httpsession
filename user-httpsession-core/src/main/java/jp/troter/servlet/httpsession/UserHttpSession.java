package jp.troter.servlet.httpsession;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.state.SessionState;

import org.apache.commons.lang.ObjectUtils;

public class UserHttpSession implements HttpSession {

    protected final UserHttpSessionHttpServletRequestWrapper request;

    protected final String id;

    protected SessionStateManager sessionStateManager;

    protected SessionState sessionState;

    protected ServletContext servletContext;

    protected final boolean isNew;

    protected int maxInactiveInterval = Integer.MAX_VALUE;

    public UserHttpSession(UserHttpSessionHttpServletRequestWrapper request, String id,
            SessionStateManager sessionStateManager, boolean isNew) {
        this.request = request;
        this.id = id;
        this.isNew = isNew;
        this.sessionStateManager = sessionStateManager;
    }

    protected synchronized SessionState getSessionState() {
        if (sessionState == null) {
            sessionState = sessionStateManager.loadState(id);
        }
        return sessionState;
    }

    @Override
    public long getCreationTime() {
        return getSessionState().getCreationTime();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        return getSessionState().getLastAccessedTime();
    }

    /**
     * NOTICE: always return null.
     */
    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Deprecated
    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return getSessionState().getAttribute(name);
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Enumeration getAttributeNames() {
        return getSessionState().getAttributeNames();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String[] getValueNames() {
        List<String> list = new ArrayList<String>();
        for (Enumeration e = getAttributeNames(); e.hasMoreElements();) {
            list.add(ObjectUtils.toString(e.nextElement()));
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public void setAttribute(String name, Object value) {
        getSessionState().setAttribute(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        setAttribute(name, null);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        sessionStateManager.removeState(id);
        request.invalidateSession();
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

}
