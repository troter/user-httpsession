package jp.troter.servlet.httpsession.spi.memcached.initializer;

import jp.troter.servlet.httpsession.spi.SingleServiceLoader;
import net.rubyeye.xmemcached.MemcachedClient;

public abstract class XMemcachedInitializer {

    private static final String SERVICE_CLASS_NAME
        = "jp.troter.servlet.httpsession.spi.memcached.initializer.XMemcachedInitializer";

    private static SingleServiceLoader<XMemcachedInitializer> loader;

    private static synchronized SingleServiceLoader<XMemcachedInitializer> getLoader() {
        if (loader == null) {
            loader = new SingleServiceLoader<XMemcachedInitializer>(SERVICE_CLASS_NAME, null);
        }
        return loader;
    }

    public static XMemcachedInitializer newInstance() {
        return getLoader().newService();
    }

    public abstract MemcachedClient getMemcachedClient();

}
