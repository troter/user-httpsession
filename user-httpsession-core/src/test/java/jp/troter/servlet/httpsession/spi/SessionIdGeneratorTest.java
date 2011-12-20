package jp.troter.servlet.httpsession.spi;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import jp.troter.servlet.httpsession.spi.support.DefaultSessionIdGenerator;

import org.junit.Test;

public class SessionIdGeneratorTest {

    @Test
    public void load() {
        SessionIdGenerator instance = SessionIdGenerator.newInstance();
        assertThat(instance, not(nullValue()));
        assertTrue(instance instanceof DefaultSessionIdGenerator);
    }

    @Test
    public void retryLimit() {
        SessionIdGenerator instance = SessionIdGenerator.newInstance();
        assertThat(instance.getRetryLimit(), is(10));
        System.setProperty(SessionIdGenerator.PROPERTY_KEY_SESSION_ID_RETRY_LIMIT, "20");
        assertThat(instance.getRetryLimit(), is(20));
    }

}
