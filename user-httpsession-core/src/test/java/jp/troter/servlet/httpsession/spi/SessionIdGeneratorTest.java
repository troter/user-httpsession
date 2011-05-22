package jp.troter.servlet.httpsession.spi;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import jp.troter.servlet.httpsession.spi.impl.DefaultSessionIdGenerator;

import org.junit.Test;

public class SessionIdGeneratorTest {

    @Test
    public void load() {
        SessionIdGenerator instance = SessionIdGenerator.newInstance();
        assertThat(instance, not(nullValue()));
        assertTrue(instance instanceof DefaultSessionIdGenerator);
    }
}
