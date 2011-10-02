package jp.troter.servlet.httpsession.spi;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public abstract class SessionCookieHandler {

    private static final String SERVICE_CLASS_NAME
        = "jp.troter.servlet.httpsession.spi.SessionCookieHandler";
    private static final String DEFAULT_IMPLEMENTATION_NAME
        = "jp.troter.servlet.httpsession.spi.impl.DefaultSessionCookieHandler";

    public static final String PROPERTY_KEY_SESSION_COOKIE_NAME
        = "jp.troter.servlet.httpsession.spi.SessionCookieHandler.sessionCookieName";

    public static final String PROPERTY_KEY_SESSION_COOKIE_DOMAIN
        = "jp.troter.servlet.httpsession.spi.SessionCookieHandler.sessionCookieDomain";

    public static final String PROPERTY_KEY_SESSION_COOKIE_PATH
        = "jp.troter.servlet.httpsession.spi.SessionCookieHandler.sessionCookiePath";

    public static final String PROPERTY_KEY_SESSION_COOKIE_SECURE
        = "jp.troter.servlet.httpsession.spi.SessionCookieHandler.sessionCookieSecure";

    public static final String DEFAULT_SESSION_COOKIE_NAME = "SESSIONID";

    private static SingleServiceLoader<SessionCookieHandler> loader;

    private static synchronized SingleServiceLoader<SessionCookieHandler> getLoader() {
        if (loader == null) {
            loader = new SingleServiceLoader<SessionCookieHandler>(SERVICE_CLASS_NAME, DEFAULT_IMPLEMENTATION_NAME);
        }
        return loader;
    }

    public static SessionCookieHandler newInstance() {
        return getLoader().newService();
    }

    /**
     * session id cookie name.
     * @return
     */
    public String getSessionCookieName() {
        String sessionCookieName = System.getProperty(PROPERTY_KEY_SESSION_COOKIE_NAME);
        if (sessionCookieName != null) {
            return sessionCookieName;
        }
        return DEFAULT_SESSION_COOKIE_NAME;
    }

    /**
     * session id cookie domain.
     * @return
     */
    public String getSessionCookieDomain() {
        String sessionCookieDomain = System.getProperty(PROPERTY_KEY_SESSION_COOKIE_DOMAIN);
        if (sessionCookieDomain != null) {
            return sessionCookieDomain;
        }
        return null;
    }

    /**
     * session id cookie path.
     * @return
     */
    public String getSessionCookiePath() {
        String sessionCookiePath = System.getProperty(PROPERTY_KEY_SESSION_COOKIE_PATH);
        if (sessionCookiePath != null) {
            return sessionCookiePath;
        }
        return null;
    }

    /**
     * session id cookie secure.
     * @return
     */
    public boolean isSecureSessionCookie() {
        String sessionCookieSecure = System.getProperty(PROPERTY_KEY_SESSION_COOKIE_SECURE);
        if (sessionCookieSecure != null) {
            return Boolean.valueOf(sessionCookieSecure).booleanValue();
        }
        return false;
    }

    /**
     * get session id from cookie
     * @param request
     * @return
     */
    public abstract String getSessionIdFromCookie(HttpServletRequest request);

    /**
     * get session id from request url
     * @param request
     * @return
     */
    public abstract String getSessionIdFromURL(HttpServletRequest request);

    /**
     * add session id parameter to url
     * @param url
     * @param request
     * @return
     */
    public abstract String rewriteURL(String url, HttpServletRequest request);

    /**
     * write session cookie
     * @param request
     * @param response
     * @param sessionId
     */
    public abstract void writeCookie(HttpServletRequest request, HttpServletResponse response, String sessionId);

    /**
     * write expire session cookie
     * @param request
     * @param response
     * @param sessionId
     */
    public abstract void expireCookie(HttpServletRequest request, HttpServletResponse response, String sessionId);

    /**
     * create session cookie
     * @param request
     * @param response
     * @param sessionId
     * @return
     */
    public abstract Cookie createSessionCookie(HttpServletRequest request,
            HttpServletResponse response, String sessionId);
}
