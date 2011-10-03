package jp.troter.servlet.httpsession.spi;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jp.troter.servlet.httpsession.state.SessionState;


public abstract class SessionStateManager {

    private static final String SERVICE_CLASS_NAME
        = "jp.troter.servlet.httpsession.spi.SessionStateManager";
    private static final String DEFAULT_IMPLEMENTATION_NAME
        = "jp.troter.servlet.httpsession.spi.impl.DefaultSessionStateManager";

    public static final String PROPERTY_KEY_SESSION_STATE_DEFAULT_TIMEOUT_SECOND
        = "jp.troter.servlet.httpsession.spi.SessionStateManager.defaultTimeoutSecond";

    public static final String PROPERTY_KEY_SESSION_STATE_THROW_EXCEPTION
        = "jp.troter.servlet.httpsession.spi.SessionStateManager.throwException";

    public static final String PROPERTY_KEY_SESSION_STATE_NAME_SPACE
        = "jp.troter.servlet.httpsession.spi.SessionStateManager.nameSpace";

    public static final int DEFAULT_DEFAULT_TIMEOUT_SECOND
        = Long.valueOf(TimeUnit.HOURS.toSeconds(1L)).intValue();

    public static final String DEFAULT_NAME_SPACE = "httpsession";

    private static SingleServiceLoader<SessionStateManager> loader;

    private static SessionStateManager sessionStateManager;

    private static synchronized SingleServiceLoader<SessionStateManager> getLoader() {
        if (loader == null) {
            loader = new SingleServiceLoader<SessionStateManager>(SERVICE_CLASS_NAME, DEFAULT_IMPLEMENTATION_NAME);
        }
        return loader;
    }

    public static synchronized SessionStateManager getInstance() {
        if (sessionStateManager == null) {
            sessionStateManager = getLoader().newService();
        }
        return sessionStateManager;
    }

    /**
     * session timeout second.
     * @return session timeout second.
     */
    public int getDefaultTimeoutSecond() {
        String defaultTimeoutSecond = System.getProperty(PROPERTY_KEY_SESSION_STATE_DEFAULT_TIMEOUT_SECOND);
        if (defaultTimeoutSecond != null) {
            return Integer.valueOf(defaultTimeoutSecond).intValue();
        }
        return DEFAULT_DEFAULT_TIMEOUT_SECOND;
    }

    /**
     * SessionStateManager throw exception.
     * @return
     */
    public boolean isThrowException() {
        String throwException = System.getProperty(PROPERTY_KEY_SESSION_STATE_THROW_EXCEPTION);
        if (throwException != null) {
            return Boolean.valueOf(throwException).booleanValue();
        }
        return false;
    }

    /**
     * httpsession name space.
     * @return
     */
    public String getNameSpace() {
        String nameSpace = System.getProperty(PROPERTY_KEY_SESSION_STATE_NAME_SPACE);
        if (nameSpace != null) {
            return nameSpace;
        }
        return DEFAULT_NAME_SPACE;
    }

    /**
     * load {@link SessionState} from miscellaneous storage.
     * @param sessionId
     * @return loaded {@link SessionState}
     */
    public abstract SessionState loadState(String sessionId);

    /**
     * save {@link SessionState} to miscellaneous storage.
     * @param sessionId
     * @param sessionState
     */
    public abstract void saveState(String sessionId, SessionState sessionState);

    /**
     * remove {@link SessionState} from miscellaneous storage.
     * @param sessionId
     */
    public abstract void removeState(String sessionId);

    /**
     * timeout millisecond.
     * @return
     */
    public long getTimeoutTime() {
        return getTimeoutTime(getDefaultTimeoutSecond());
    }

    /**
     * timeout millisecond.
     * @param maxInactiveInterval
     * @return
     */
    public long getTimeoutTime(int maxInactiveInterval) {
        return new Date().getTime() + TimeUnit.SECONDS.toMillis(maxInactiveInterval);
    }

    public static class Cell implements Serializable {
        private static final long serialVersionUID = 1L;

        Map<String, Object> attributes;
        long lastAccessedTime;
        int maxInactiveInterval;

        public Cell(Map<String, Object> attributes, long lastAccessedTime, int maxInactiveInterval) {
            this.attributes = attributes;
            this.lastAccessedTime = lastAccessedTime;
            this.maxInactiveInterval = maxInactiveInterval;
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
