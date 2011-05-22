package jp.troter.servlet.httpsession.spi;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import jp.troter.servlet.httpsession.spi.impl.DefaultSessionValidator;

import org.junit.Test;

public class SessionValidatorTest {

    @Test
    public void load() {
        SessionValidator instance = SessionValidator.newInstance();
        assertThat(instance, not(nullValue()));
        assertTrue(instance instanceof DefaultSessionValidator);
    }
}
