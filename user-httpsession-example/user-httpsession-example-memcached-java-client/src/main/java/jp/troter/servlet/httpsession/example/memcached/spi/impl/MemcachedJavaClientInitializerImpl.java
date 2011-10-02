package jp.troter.servlet.httpsession.example.memcached.spi.impl;

import java.util.concurrent.TimeUnit;

import jp.troter.servlet.httpsession.spi.MemcachedJavaClientInitializer;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemcachedJavaClientInitializerImpl extends MemcachedJavaClientInitializer {

    MemCachedClient c = null;

    @Override
    public MemCachedClient getMemCachedClient() {
        if (c == null) {
            SockIOPool pool = SockIOPool.getInstance();
            pool.setServers(new String[] {"localhost:11211"});
            pool.setWeights(new Integer[] {1});
            pool.initialize();
            c = new MemCachedClient();
        }
        return c;
    }

    @Override
    public int getDefaultTimeoutSecond() {
        return Long.valueOf(TimeUnit.HOURS.toSeconds(1L)).intValue();
    }

}
