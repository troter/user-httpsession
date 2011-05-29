package jp.troter.servlet.httpsession.example.jredis.spi.impl;

import java.util.concurrent.TimeUnit;

import jp.troter.servlet.httpsession.spi.JRedisInitializer;

import org.jredis.JRedis;
import org.jredis.RedisException;
import org.jredis.ri.alphazero.JRedisClient;

public class JRedisInitializerImpl extends JRedisInitializer {

    JRedis jredis;

    @Override
    public JRedis getJRedis() {
        if (jredis == null) {
            try {
                jredis = new JRedisClient("localhost", 6379);
                jredis.ping();
            } catch (RedisException e) {
            }
        }
        return jredis;
    }

    @Override
    public int getDefaultTimeoutSecond() {
        return Long.valueOf(TimeUnit.HOURS.toSeconds(1L)).intValue();
    }

}
