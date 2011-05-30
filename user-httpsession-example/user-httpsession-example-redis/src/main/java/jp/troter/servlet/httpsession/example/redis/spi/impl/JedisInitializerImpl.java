package jp.troter.servlet.httpsession.example.redis.spi.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.pool.impl.GenericObjectPool.Config;

import jp.troter.servlet.httpsession.spi.JedisInitializer;
import redis.clients.jedis.JedisPool;

public class JedisInitializerImpl extends JedisInitializer {

    JedisPool p;

    @Override
    public JedisPool getJedisPool() {
        if (p == null) {
            p = new JedisPool(new Config(), "localhost", 6379);
        }
        return p;
    }

    @Override
    public int getDefaultTimeoutSecond() {
        return Long.valueOf(TimeUnit.HOURS.toSeconds(1L)).intValue();
    }

}
