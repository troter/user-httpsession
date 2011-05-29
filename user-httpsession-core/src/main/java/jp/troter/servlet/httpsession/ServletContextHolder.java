package jp.troter.servlet.httpsession;

import javax.servlet.ServletContext;

public class ServletContextHolder {

    private static ServletContextHolder instance;

    public synchronized static ServletContextHolder getInstance() {
        if (instance == null) {
            instance = new ServletContextHolder();
        }
        return instance;
    }

    @Deprecated
    protected synchronized static void setInstance(ServletContextHolder _instance) {
        instance = _instance;
    }

    private ServletContext servletContext;

    private ServletContextHolder() {
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
