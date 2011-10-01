package jp.troter.servlet.httpsession.example.redis;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import jp.troter.servlet.httpsession.example.redis.resources.RootResource;

public class RedisExampleApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(RootResource.class);
        return s;
    }
}
