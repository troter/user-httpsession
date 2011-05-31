package jp.troter.servlet.httpsession;

import javax.servlet.ServletContext;

public class ServletContextHolder {

    private static ServletContextHolder instance;

    public synchronized static ServletContextHolder getInstance() {
        return instance;
    }

    protected synchronized static void setInstance(ServletContextHolder _instance) {
        instance = _instance;
    }

    private final ServletContext servletContext;

    protected ServletContextHolder(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}
