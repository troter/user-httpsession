package jp.troter.servlet.httpsession.spi.impl;

import java.util.UUID;

import jp.troter.servlet.httpsession.spi.SessionIdGenerator;
import jp.troter.servlet.httpsession.wrapper.UserHttpSessionHttpServletRequestWrapper;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSessionIdGenerator extends SessionIdGenerator{

    private static Logger log = LoggerFactory.getLogger(DefaultSessionIdGenerator.class);

    private static final int RETRY_LIMIT = 10;

    @Override
    public int getRetryLimit() {
        String retryLimit = System.getProperty(PROPERTY_KEY_SESSION_ID_RETRY_LIMIT);
        if (retryLimit != null) {
            return Integer.valueOf(retryLimit).intValue();
        }
        return RETRY_LIMIT;
    }

    @Override
    public String generateSessionId(UserHttpSessionHttpServletRequestWrapper request) {
        return generateSessionId(request, -1);
    }

    @Override
    public String generateSessionId(UserHttpSessionHttpServletRequestWrapper request, int retry) {
        String seed = String.format("%s:%s:%s:%s:%s",
                getUUIDString(), getHostName(), getThreadIdString(), request.getRemoteAddr(), String.valueOf(retry));
        return DigestUtils.sha512Hex(seed);
    }

    protected String getUUIDString() {
        return UUID.randomUUID().toString();
    }

    protected String getHostName() {
        String hostname = "";
        try {
            hostname = java.net.InetAddress.getLocalHost().getHostName();
        } catch (java.net.UnknownHostException e) {
            log.warn("Can not get local host name", e);
        }
        return hostname;
    }

    protected long getThreadId() {
        return Thread.currentThread().getId();
    }

    protected String getThreadIdString() {
        return Long.valueOf(getThreadId()).toString();
    }
}
