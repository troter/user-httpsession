package jp.troter.servlet.httpsession.example.memcached;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import jp.troter.servlet.httpsession.example.memcached.resources.RootResource;

public class MemcachedExampleApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(RootResource.class);
        return s;
    }
}
