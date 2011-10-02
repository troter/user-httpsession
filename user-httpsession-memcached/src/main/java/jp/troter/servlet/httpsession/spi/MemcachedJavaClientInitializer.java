package jp.troter.servlet.httpsession.spi;

import com.danga.MemCached.MemCachedClient;

public abstract class MemcachedJavaClientInitializer {

    private static final String SERVICE_CLASS_NAME
        = "jp.troter.servlet.httpsession.spi.MemcachedJavaClientInitializer";

    private static SingleServiceLoader<MemcachedJavaClientInitializer> loader;

    private static synchronized SingleServiceLoader<MemcachedJavaClientInitializer> getLoader() {
        if (loader == null) {
            loader = new SingleServiceLoader<MemcachedJavaClientInitializer>(SERVICE_CLASS_NAME, null);
        }
        return loader;
    }

    public static MemcachedJavaClientInitializer newInstance() {
        return getLoader().newService();
    }

    public abstract MemCachedClient getMemCachedClient();
}
