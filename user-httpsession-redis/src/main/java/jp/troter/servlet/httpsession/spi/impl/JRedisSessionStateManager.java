package jp.troter.servlet.httpsession.spi.impl;

import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.state.SessionState;

public class JRedisSessionStateManager extends SessionStateManager {

    @Override
    public SessionState loadState(String sessionId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateState(String sessionId, SessionState sessionState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeState(String sessionId) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getTimeoutSecond() {
        return 0;
    }
}
