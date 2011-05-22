package jp.troter.servlet.httpsession.spi;

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

    public abstract SessionState loadState(String sessionId);

    public abstract void updateState(String sessionId, SessionState sessionState);

    public abstract void removeState(String sessionId);
}
