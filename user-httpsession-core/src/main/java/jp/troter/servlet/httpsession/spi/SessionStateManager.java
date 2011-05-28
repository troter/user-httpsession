package jp.troter.servlet.httpsession.spi;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import jp.troter.servlet.httpsession.state.SessionState;


public abstract class SessionStateManager {

    private static final String SERVICE_CLASS_NAME
        = "jp.troter.servlet.httpsession.spi.SessionStateManager";
    private static final String DEFAULT_IMPLEMENTATION_NAME
        = "jp.troter.servlet.httpsession.spi.impl.DefaultSessionStateManager";

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
     * session timeout second.
     * @return session timeout second.
     */
    public abstract int getTimeoutSecond();

    /**
     * timeout millisecond.
     * @return
     */
    public long getTimeoutTime() {
        return new Date().getTime() + TimeUnit.SECONDS.toMillis(getTimeoutSecond());
    }
}
