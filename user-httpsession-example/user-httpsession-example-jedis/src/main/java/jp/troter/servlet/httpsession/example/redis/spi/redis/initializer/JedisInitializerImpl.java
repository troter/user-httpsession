package jp.troter.servlet.httpsession.example.redis.spi.redis.initializer;

import jp.troter.servlet.httpsession.spi.redis.initializer.JedisInitializer;

import org.apache.commons.pool.impl.GenericObjectPool;

import redis.clients.jedis.JedisPool;

public class JedisInitializerImpl extends JedisInitializer {

    JedisPool p;

    @Override
    public JedisPool getJedisPool() {
        if (p == null) {
            // ref https://github.com/xetorthio/jedis/issues/68#issuecomment-639024
            GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
            poolConfig.testWhileIdle = true;
            poolConfig.minEvictableIdleTimeMillis = 60000;
            poolConfig.timeBetweenEvictionRunsMillis = 30000;
            poolConfig.numTestsPerEvictionRun = -1;
            p = new JedisPool(poolConfig, "localhost", 6379);
        }
        return p;
    }
}
