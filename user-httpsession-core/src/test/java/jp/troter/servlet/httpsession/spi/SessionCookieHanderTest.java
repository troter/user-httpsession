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
}
