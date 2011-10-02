package jp.troter.servlet.httpsession;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;

public class UserHttpSessionListenerHolder {

    public static List<HttpSessionAttributeListener> httpSessionAttributeListeners
        = new CopyOnWriteArrayList<HttpSessionAttributeListener>();

    public static List<HttpSessionListener> httpSessionListeners
        = new CopyOnWriteArrayList<HttpSessionListener>();
}
