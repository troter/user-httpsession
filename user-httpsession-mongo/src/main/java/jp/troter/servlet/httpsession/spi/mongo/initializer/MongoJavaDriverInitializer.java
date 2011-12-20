package jp.troter.servlet.httpsession.spi.mongo.initializer;

import jp.troter.servlet.httpsession.spi.SingleServiceLoader;

import com.mongodb.DB;

public abstract class MongoJavaDriverInitializer {

    private static final String SERVICE_CLASS_NAME
        = "jp.troter.servlet.httpsession.spi.mongo.initializer.MongoJavaDriverInitializer";

    private static SingleServiceLoader<MongoJavaDriverInitializer> loader;

    private static synchronized SingleServiceLoader<MongoJavaDriverInitializer> getLoader() {
        if (loader == null) {
            loader = new SingleServiceLoader<MongoJavaDriverInitializer>(SERVICE_CLASS_NAME, null);
        }
        return loader;
    }

    public static MongoJavaDriverInitializer newInstance() {
        return getLoader().newService();
    }

    public abstract DB getDB();
}
