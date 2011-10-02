package jp.troter.servlet.httpsession.spi;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import jp.troter.servlet.httpsession.spi.impl.DefaultSessionStateManager;

import org.junit.Test;

public class SessionStateManagerTest {

    @Test
    public void load() {
        SessionStateManager instance = SessionStateManager.getInstance();
        assertThat(instance, not(nullValue()));
        assertTrue(instance instanceof DefaultSessionStateManager);
    }

    @Test
    public void defaultTimeoutSecond() {
        SessionStateManager instance = SessionStateManager.getInstance();
        assertThat(instance.getDefaultTimeoutSecond(), is(3600));
        System.setProperty(SessionStateManager.PROPERTY_KEY_SESSION_STATE_DEFAULT_TIMEOUT_SECOND, "20");
        assertThat(instance.getDefaultTimeoutSecond(), is(20));
    }

}
