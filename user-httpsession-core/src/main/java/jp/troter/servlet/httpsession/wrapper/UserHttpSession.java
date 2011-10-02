package jp.troter.servlet.httpsession.wrapper;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import jp.troter.servlet.httpsession.ServletContextHolder;
import jp.troter.servlet.httpsession.UserHttpSessionListenerHolder;
import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.state.SessionState;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserHttpSession implements HttpSession {

    private static Logger log = LoggerFactory.getLogger(UserHttpSession.class);

    protected final UserHttpSessionHttpServletRequestWrapper request;

    protected final String id;

    protected SessionStateManager sessionStateManager;

    protected SessionState sessionState;

    protected int maxInactiveInterval = Integer.MAX_VALUE;

    public UserHttpSession(UserHttpSessionHttpServletRequestWrapper request, String id,
            SessionStateManager sessionStateManager) {
        this.request = request;
        this.id = id;
        this.sessionStateManager = sessionStateManager;
        if (isNew()) {
            notifySessionCreated();
        }
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

    @Override
    public ServletContext getServletContext() {
        return ServletContextHolder.getInstance().getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        getSessionState().setMaxInactiveInterval(interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return getSessionState().getMaxInactiveInterval();
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
        if (value == null) {
            removeAttribute(name);
            return;
        }
        Object unbound = getSessionState().getAttribute(name);
        getSessionState().setAttribute(name, value);

        notifyValueUnbound(name, value, unbound);
        notifyValueBound(name, value, unbound);
        notifyAttributeAddedOrAttributeReplaced(name, value, unbound);
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        Object value = getSessionState().getAttribute(name);
        if (value == null) {
            return;
        }

        getSessionState().removeAttribute(name);

        notifyValueUnbound(name, null, value);
        notifyAttributeRemoved(name, value);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        notifySessionDestroyed();
        sessionStateManager.removeState(id);
        request.invalidateSession();
    }

    @Override
    public boolean isNew() {
        return getSessionState().isNew();
    }

    public void notifyValueUnbound(String name, Object value, Object unbound) {
        if ((unbound != null) && (unbound != value) &&
                (unbound instanceof HttpSessionBindingListener)) {
            HttpSessionBindingEvent event = new HttpSessionBindingEvent(this, name, value);
            try {
                ((HttpSessionBindingListener) unbound).valueUnbound(event);
            } catch (Throwable t) {
                    String message = String.format("Exception invoking valueUnbound() on HttpSessionBindingListener: %s", unbound.getClass().getName());
                    log.warn(message, t);
            }
        }
    }

    public void notifyValueBound(String name, Object value, Object unbound) {
        if (value instanceof HttpSessionBindingListener) {
            if (value != unbound) {
                HttpSessionBindingEvent event = new HttpSessionBindingEvent(this, name, unbound);
                try {
                    ((HttpSessionBindingListener) value).valueBound(event);
                } catch (Throwable t) {
                    String message = String.format("Exception invoking valueBound() on HttpSessionBindingListener: %s", value.getClass().getName());
                    log.warn(message, t);
                }
            }
        }
    }

    public void notifyAttributeAddedOrAttributeReplaced(String name, Object value, Object unbound) {
        HttpSessionBindingEvent event = null;
        for (HttpSessionAttributeListener listener : UserHttpSessionListenerHolder.httpSessionAttributeListeners) {
            try {
                if (unbound != null) {
                    if (event == null) {
                        event = new HttpSessionBindingEvent(this, name, unbound);
                    }
                    listener.attributeReplaced(event);
                } else {
                    if (event == null) {
                        event = new HttpSessionBindingEvent(this, name, value);
                    }
                    listener.attributeAdded(event);
                }
            } catch (Throwable t) {
                String message = String.format("Exception invoking attributeAdded() or attributeReplaced() on HttpSessionAttributeListener: %s", listener.getClass().getName());
                log.warn(message, t);
            }
        }
    }

    public void notifyAttributeRemoved(String name, Object value) {
        HttpSessionBindingEvent event = null;
        for (HttpSessionAttributeListener listener : UserHttpSessionListenerHolder.httpSessionAttributeListeners) {
            try {
                if (event == null) {
                    event = new HttpSessionBindingEvent(this, name, value);
                }
                listener.attributeRemoved(event);
            } catch (Throwable t) {
                String message = String.format("Exception invoking attributeRemoved() on HttpSessionAttributeListener: %s", listener.getClass().getName());
                log.warn(message, t);
            }
        }
    }

    public void notifySessionCreated() {
        HttpSessionEvent event = new HttpSessionEvent(this);
        for (HttpSessionListener listener : UserHttpSessionListenerHolder.httpSessionListeners) {
            try {
                listener.sessionCreated(event);
            } catch (Throwable t) {
                String message = String.format("Exception invoking sessionCreated() on HttpSessionListener: %s", listener.getClass().getName());
                log.warn(message, t);
            }
        }
    }

    public void notifySessionDestroyed() {
        HttpSessionEvent event = new HttpSessionEvent(this);
        for (HttpSessionListener listener : UserHttpSessionListenerHolder.httpSessionListeners) {
            try {
                listener.sessionDestroyed(event);
            } catch (Throwable t) {
                String message = String.format("Exception invoking sessionDestroyed() on HttpSessionListener: %s", listener.getClass().getName());
                log.warn(message, t);
            }
        }
    }
}
