package jp.troter.servlet.httpsession.spi.impl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.troter.servlet.httpsession.spi.SessionCookieHandler;

import org.apache.commons.lang.StringUtils;

public class DefaultSessionCookieHandler extends SessionCookieHandler {

    private static final String SESSION_COOKIE_NAME = "SESSIONID";

    @Override
    public String getSessionCookieName() {
        return SESSION_COOKIE_NAME;
    }

    @Override
    public String getSessionCookieDomain() {
        return null;
    }

    @Override
    public String getSessionCookiePath() {
        return null;
    }

    @Override
    public boolean isSecureSessionCookie() {
        return false;
    }

    protected String getPartOfUri() {
        return ";" + getSessionCookieName() + "=";
    }

    @Override
    public String getSessionIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            if (cookie.getName().equals(getSessionCookieName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public String getSessionIdFromURL(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int index = uri.lastIndexOf(getPartOfUri());
        if (index < 0) {
            return null;
        }
        uri = uri.substring(index + getPartOfUri().length());
        int index2 = uri.indexOf('?');
        if (index2 < 0) {
            return uri;
        }
        return uri.substring(0, index2);
    }

    @Override
    public String rewriteURL(String url, HttpServletRequest request) {
        if (request.isRequestedSessionIdFromCookie()) {
            return url;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return url;
        }
        int index = url.indexOf('?');
        if (index < 0) {
            return url + getPartOfUri() + session.getId();
        } else {
            return url.substring(0, index) + getPartOfUri() + session.getId()
                    + url.substring(index);
        }
    }

    @Override
    public void writeCookie(HttpServletRequest request,
            HttpServletResponse response, String sessionId) {
        if (request.isRequestedSessionIdFromCookie()) {
            return;
        }
        Cookie cookie = createSessionCookie(request, response, sessionId);
        response.addCookie(cookie);
    }

    @Override
    public void expireCookie(HttpServletRequest request,
            HttpServletResponse response, String sessionId) {
        if (request.isRequestedSessionIdFromCookie()) {
            return;
        }
        Cookie cookie = createSessionCookie(request, response, sessionId);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @Override
    public Cookie createSessionCookie(HttpServletRequest request,
            HttpServletResponse response, String sessionId) {
        Cookie cookie = new Cookie(getSessionCookieName(), sessionId);

        if (! StringUtils.isEmpty(getSessionCookieDomain())) {
            cookie.setDomain(getSessionCookieDomain());
        }

        String path = request.getContextPath();
        if (! StringUtils.isEmpty(getSessionCookiePath())) {
            path = getSessionCookiePath();
        }
        cookie.setPath(StringUtils.isEmpty(path) ? "/" : path);

        cookie.setSecure(isSecureSessionCookie());
        return cookie;
    }
}
