package jp.troter.servlet.httpsession.spi.impl;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import jp.troter.servlet.httpsession.UserHttpSessionHttpServletRequestWrapper;
import jp.troter.servlet.httpsession.spi.SessionIdGenerator;

public class DefaultSessionIdGenerator extends SessionIdGenerator{

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
            // TODO logging
            hostname = "";
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
