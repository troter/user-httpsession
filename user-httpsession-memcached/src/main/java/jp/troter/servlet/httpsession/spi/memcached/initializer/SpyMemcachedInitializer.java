package jp.troter.servlet.httpsession.spi.memcached.initializer;

import jp.troter.servlet.httpsession.spi.SingleServiceLoader;
import net.spy.memcached.MemcachedClient;

public abstract class SpyMemcachedInitializer {

    private static final String SERVICE_CLASS_NAME
        = "jp.troter.servlet.httpsession.spi.memcached.initializer.SpyMemcachedInitializer";

    private static SingleServiceLoader<SpyMemcachedInitializer> loader;

    private static synchronized SingleServiceLoader<SpyMemcachedInitializer> getLoader() {
        if (loader == null) {
            loader = new SingleServiceLoader<SpyMemcachedInitializer>(SERVICE_CLASS_NAME, null);
        }
        return loader;
    }

    public static SpyMemcachedInitializer newInstance() {
        return getLoader().newService();
    }

    public abstract MemcachedClient getMemcachedClient();
}
