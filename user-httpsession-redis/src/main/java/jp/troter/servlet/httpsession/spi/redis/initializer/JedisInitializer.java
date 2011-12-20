package jp.troter.servlet.httpsession.spi.redis.initializer;

import jp.troter.servlet.httpsession.spi.SingleServiceLoader;
import redis.clients.jedis.JedisPool;

public abstract class JedisInitializer {

    private static final String SERVICE_CLASS_NAME
        = "jp.troter.servlet.httpsession.spi.redis.initializer.JedisInitializer";

    private static SingleServiceLoader<JedisInitializer> loader;

    private static synchronized SingleServiceLoader<JedisInitializer> getLoader() {
        if (loader == null) {
            loader = new SingleServiceLoader<JedisInitializer>(SERVICE_CLASS_NAME, null);
        }
        return loader;
    }

    public static JedisInitializer newInstance() {
        return getLoader().newService();
    }

    public abstract JedisPool getJedisPool();
}
