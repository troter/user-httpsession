package jp.troter.servlet.httpsession.spi;

import jp.troter.servlet.httpsession.wrapper.UserHttpSessionHttpServletRequestWrapper;

public abstract class SessionIdGenerator {

    private static final String SERVICE_CLASS_NAME
        = "jp.troter.servlet.httpsession.spi.SessionIdGenerator";
    private static final String DEFAULT_IMPLEMENTATION_NAME
        = "jp.troter.servlet.httpsession.spi.impl.DefaultSessionIdGenerator";

    private static SingleServiceLoader<SessionIdGenerator> loader;

    private static synchronized SingleServiceLoader<SessionIdGenerator> getLoader() {
        if (loader == null) {
            loader = new SingleServiceLoader<SessionIdGenerator>(SERVICE_CLASS_NAME, DEFAULT_IMPLEMENTATION_NAME);
        }
        return loader;
    }

    public static SessionIdGenerator newInstance() {
        return getLoader().newService();
    }

    /**
     * generate session id
     * @param request
     * @return
     */
    public abstract String generateSessionId(UserHttpSessionHttpServletRequestWrapper request);

    /**
     * generate session id
     * @param request
     * @param retry
     * @return
     */
    public abstract String generateSessionId(UserHttpSessionHttpServletRequestWrapper request, int retry);
}
