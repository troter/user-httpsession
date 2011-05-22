package jp.troter.servlet.httpsession.spi;

public abstract class JRedisInitializer {

    private static final String SERVICE_CLASS_NAME
        = "jp.troter.servlet.httpsession.spi.JRedisInitializer";

    private static SingleServiceLoader<JRedisInitializer> loader;

    private static synchronized SingleServiceLoader<JRedisInitializer> getLoader() {
        if (loader == null) {
            loader = new SingleServiceLoader<JRedisInitializer>(SERVICE_CLASS_NAME, null);
        }
        return loader;
    }

    public static JRedisInitializer newInstance() {
        return getLoader().newService();
    }

    //public abstract JRedis getJRedis();
}
