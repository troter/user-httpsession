package jp.troter.servlet.httpsession;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;

public class UserHttpSessionListenerHolder {

    private static List<HttpSessionAttributeListener> httpSessionAttributeListeners
        = new CopyOnWriteArrayList<HttpSessionAttributeListener>();

    private static List<HttpSessionListener> httpSessionListeners
        = new CopyOnWriteArrayList<HttpSessionListener>();

    public static void addHttpSessionAttributeListener(HttpSessionAttributeListener listener) {
        httpSessionAttributeListeners.add(listener);
    }

    public static List<HttpSessionAttributeListener> getHttpSessionAttributeListeners() {
        return httpSessionAttributeListeners;
    }

    public static void addHttpSessionListener(HttpSessionListener listener) {
        httpSessionListeners.add(listener);
    }

    public static List<HttpSessionListener> getHttpSessionListeners() {
        return httpSessionListeners;
    }
}
