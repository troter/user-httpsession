package jp.troter.servlet.httpsession.spi.impl;

import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.state.SessionState;

public class DefaultSessionStateManager extends SessionStateManager {

    @Override
    public void updateState(String sessionId, SessionState sessionState) {
    }

    @Override
    public void removeState(String sessionId) {
    }

    @Override
    public SessionState loadState(String sessionId) {
        return null;
    }

}
