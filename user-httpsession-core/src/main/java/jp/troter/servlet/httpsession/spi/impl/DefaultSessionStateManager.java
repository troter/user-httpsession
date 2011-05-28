package jp.troter.servlet.httpsession.spi.impl;

import java.util.Date;
import java.util.HashMap;

import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.state.DefaultSessionState;
import jp.troter.servlet.httpsession.state.SessionState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSessionStateManager extends SessionStateManager {

    private static Logger log = LoggerFactory.getLogger(DefaultSessionStateManager.class);

    @Override
    public void saveState(String sessionId, SessionState sessionState) {
        log.error("DefaultSessionStateManager.saveState is stub.");
    }

    @Override
    public void removeState(String sessionId) {
        log.error("DefaultSessionStateManager.removeState is stub.");
    }

    @Override
    public SessionState loadState(String sessionId) {
        log.error("DefaultSessionStateManager.loadState is stub.");
        return new DefaultSessionState(new HashMap<String, Object>(), new Date().getTime(), true);
    }

    @Override
    public int getTimeoutSecond() {
        log.error("DefaultSessionStateManager.getTimeoutSecond is stub.");
        return 0;
    }
}
