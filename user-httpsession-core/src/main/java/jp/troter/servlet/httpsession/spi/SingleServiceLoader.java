package jp.troter.servlet.httpsession.spi;

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public class SingleServiceLoader<T> {

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

            if (defaultImplementationName != null) {
                Class<?> implClass = Class.forName(defaultImplementationName, true, Thread.currentThread().getContextClassLoader());
                service = service == null ? (T)implClass.newInstance() : service;
            }
        } catch (ClassNotFoundException x) {
            // TODO
        } catch (Exception x) {
            // TODO
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
            // TODO
        }
        return theInstance;
    }
}
