package jp.troter.servlet.httpsession.example.core;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import jp.troter.servlet.httpsession.example.core.resources.RootResource;

public class CoreExampleApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(RootResource.class);
        return s;
    }
}
