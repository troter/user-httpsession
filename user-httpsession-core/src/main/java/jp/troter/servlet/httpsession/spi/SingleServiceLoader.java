package jp.troter.servlet.httpsession.spi;

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import jp.troter.servlet.httpsession.exception.UserHttpSessionClassNotFoundRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleServiceLoader<T> {

    private static Logger log = LoggerFactory.getLogger(SingleServiceLoader.class);

    protected final String serviceClassName;

    protected final String defaultImplementationName;

    public SingleServiceLoader(String serviceClassName, String defaultImplementationName) {
        this.serviceClassName = serviceClassName;
        this.defaultImplementationName = defaultImplementationName;
    }

    @SuppressWarnings("unchecked")
    public T newService() {
        T service = null;
        try {
            Class<?> serviceClass = Class.forName(serviceClassName, true, Thread.currentThread().getContextClassLoader());
            service = loadViaServiceLoader((Class<T>)serviceClass);
        } catch (ClassNotFoundException x) {
            throw new UserHttpSessionClassNotFoundRuntimeException(
                    "Can not found service class: " + serviceClassName, x);
        }
        try {
            if (defaultImplementationName != null) {
                Class<?> implClass = Class.forName(defaultImplementationName, true, Thread.currentThread().getContextClassLoader());
                service = service == null ? (T)implClass.newInstance() : service;
            }
        } catch (ClassNotFoundException x) {
            throw new UserHttpSessionClassNotFoundRuntimeException(
                    "Can not found service class default implementation: " + defaultImplementationName, x);
        } catch (InstantiationException x) {
            throw new UserHttpSessionClassNotFoundRuntimeException(
                    "Can not found service class default implementation: " + defaultImplementationName, x);
        } catch (IllegalAccessException x) {
            throw new UserHttpSessionClassNotFoundRuntimeException(
                    "Can not found service class default implementation: " + defaultImplementationName, x);
        }
        if (service == null) {
            throw new UserHttpSessionClassNotFoundRuntimeException(
                    "Should setup service class: " + serviceClassName);
        }
        return service;
    }

    public T loadViaServiceLoader(Class<T> clazz) {
        T theInstance = null;
        try {
            for (T instance : ServiceLoader.load(clazz)) {
                theInstance = instance;
                break;
            }
        } catch (ServiceConfigurationError e) {
            log.debug("Cannot load service: " + clazz.getName());
        }
        return theInstance;
    }
}
