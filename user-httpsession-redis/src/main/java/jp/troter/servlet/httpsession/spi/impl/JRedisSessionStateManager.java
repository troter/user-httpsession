package jp.troter.servlet.httpsession.spi.impl;

import jp.troter.servlet.httpsession.spi.JRedisInitializer;
import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.state.SessionState;

public class JRedisSessionStateManager extends SessionStateManager {

    protected JRedisInitializer initializer;

    @Override
    public SessionState loadState(String sessionId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveState(String sessionId, SessionState sessionState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeState(String sessionId) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getTimeoutSecond() {
        return getInitializer().getSessionTimeout();
    }
    
    protected JRedisInitializer getInitializer() {
        if (initializer == null) {
            initializer = JRedisInitializer.newInstance();
        }
        return initializer;
    }

}
