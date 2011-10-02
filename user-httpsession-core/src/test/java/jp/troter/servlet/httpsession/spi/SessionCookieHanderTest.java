package jp.troter.servlet.httpsession.spi;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import jp.troter.servlet.httpsession.spi.impl.DefaultSessionCookieHandler;

import org.junit.Test;

public class SessionCookieHanderTest {

    @Test
    public void load() {
        SessionCookieHandler handler = SessionCookieHandler.newInstance();
        assertThat(handler, not(nullValue()));
        assertTrue(handler instanceof DefaultSessionCookieHandler);
    }

    @Test
    public void cookieName() {
        SessionCookieHandler handler = SessionCookieHandler.newInstance();
        assertThat(handler.getSessionCookieName(), is("SESSIONID"));
        System.setProperty(SessionCookieHandler.PROPERTY_KEY_SESSION_COOKIE_NAME, "session");
        assertThat(handler.getSessionCookieName(), is("session"));
    }

    @Test
    public void cookieDomain() {
        SessionCookieHandler handler = SessionCookieHandler.newInstance();
        assertThat(handler.getSessionCookieDomain(), is(nullValue()));
        System.setProperty(SessionCookieHandler.PROPERTY_KEY_SESSION_COOKIE_DOMAIN, "example.com");
        assertThat(handler.getSessionCookieDomain(), is("example.com"));
    }

    @Test
    public void cookiePath() {
        SessionCookieHandler handler = SessionCookieHandler.newInstance();
        assertThat(handler.getSessionCookiePath(), is(nullValue()));
        System.setProperty(SessionCookieHandler.PROPERTY_KEY_SESSION_COOKIE_PATH, "/user-httpsession-core-test/");
        assertThat(handler.getSessionCookiePath(), is("/user-httpsession-core-test/"));
    }

    @Test
    public void cookieSecure() {
        SessionCookieHandler handler = SessionCookieHandler.newInstance();
        assertThat(handler.isSecureSessionCookie(), is(false));
        System.setProperty(SessionCookieHandler.PROPERTY_KEY_SESSION_COOKIE_SECURE, "true");
        assertThat(handler.isSecureSessionCookie(), is(true));
        System.setProperty(SessionCookieHandler.PROPERTY_KEY_SESSION_COOKIE_SECURE, "false");
        assertThat(handler.isSecureSessionCookie(), is(false));
    }
}
