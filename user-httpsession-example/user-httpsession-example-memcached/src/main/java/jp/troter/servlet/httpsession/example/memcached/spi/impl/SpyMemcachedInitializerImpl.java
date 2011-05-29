package jp.troter.servlet.httpsession.example.memcached.spi.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import jp.troter.servlet.httpsession.spi.SpyMemcachedInitializer;
import net.spy.memcached.MemcachedClient;

public class SpyMemcachedInitializerImpl extends SpyMemcachedInitializer {

    MemcachedClient c = null;

    @Override
    public MemcachedClient getMemcachedClient() {
        if (c == null) {
            try {
                c = new MemcachedClient(new InetSocketAddress("localhost", 11211));
            } catch (IOException e) {
                return null;
            }
        }
        return c;
    }

    @Override
    public int getDefaultTimeoutSecond() {
        return Long.valueOf(TimeUnit.HOURS.toSeconds(1L)).intValue();
    }

}
