package jp.troter.servlet.httpsession.example.core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import jp.troter.servlet.httpsession.spi.SessionCookieHandler;

public class UserHttpSessionInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.setProperty(SessionCookieHandler.PROPERTY_KEY_SESSION_COOKIE_NAME, "session");
        System.setProperty(SessionCookieHandler.PROPERTY_KEY_SESSION_COOKIE_DOMAIN, "example.com");
        System.setProperty(SessionCookieHandler.PROPERTY_KEY_SESSION_COOKIE_PATH, "/example/");
        System.setProperty(SessionCookieHandler.PROPERTY_KEY_SESSION_COOKIE_SECURE, "true");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
