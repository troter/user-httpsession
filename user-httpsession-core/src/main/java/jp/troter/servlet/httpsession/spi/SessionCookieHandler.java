package jp.troter.servlet.httpsession.spi;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public abstract class SessionCookieHandler {

    private static final String SERVICE_CLASS_NAME
        = "jp.troter.servlet.httpsession.spi.SessionCookieHandler";
    private static final String DEFAULT_IMPLEMENTATION_NAME
        = "jp.troter.servlet.httpsession.spi.impl.DefaultSessionCookieHandler";

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
    public abstract String getSessionCookieName();

    /**
     * session id cookie domain.
     * @return
     */
    public abstract String getSessionCookieDomain();

    /**
     * session id cookie path.
     * @return
     */
    public abstract String getSessionCookiePath();

    /**
     * session id cookie secure.
     * @return
     */
    public abstract boolean isSecureSessionCookie();

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
