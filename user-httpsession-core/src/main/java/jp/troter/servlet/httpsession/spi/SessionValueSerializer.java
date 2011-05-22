package jp.troter.servlet.httpsession.spi;

import java.io.Serializable;

import jp.troter.servlet.httpsession.exception.UserHttpSessionSerializationException;

public abstract class SessionValueSerializer {

    private static final String SERVICE_CLASS_NAME
        = "jp.troter.servlet.httpsession.spi.SessionValueSerializer";
    private static final String DEFAULT_IMPLEMENTATION_NAME
        = "jp.troter.servlet.httpsession.spi.impl.DefaultSessionValueSerializer";

    private static SingleServiceLoader<SessionValueSerializer> loader;

    private static synchronized SingleServiceLoader<SessionValueSerializer> getLoader() {
        if (loader == null) {
            loader = new SingleServiceLoader<SessionValueSerializer>(SERVICE_CLASS_NAME, DEFAULT_IMPLEMENTATION_NAME);
        }
        return loader;
    }

    public static SessionValueSerializer newInstance() {
        return getLoader().newService();
    }

    public Object clone(final Serializable o) throws UserHttpSessionSerializationException {
        byte[] binary = serialize(o);
        return deserialize(binary);
    }

    public abstract Object deserialize(byte[] objectData) throws UserHttpSessionSerializationException,IllegalArgumentException;

    public abstract byte[] serialize(Serializable obj) throws UserHttpSessionSerializationException;
}
