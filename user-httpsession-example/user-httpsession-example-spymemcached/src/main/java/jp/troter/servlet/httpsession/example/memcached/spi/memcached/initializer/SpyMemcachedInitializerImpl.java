package jp.troter.servlet.httpsession.example.memcached.spi.memcached.initializer;

import java.io.IOException;
import java.net.InetSocketAddress;

import jp.troter.servlet.httpsession.spi.memcached.initializer.SpyMemcachedInitializer;
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
}
