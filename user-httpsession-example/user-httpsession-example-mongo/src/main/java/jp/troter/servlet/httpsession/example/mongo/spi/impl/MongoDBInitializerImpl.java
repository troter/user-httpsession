package jp.troter.servlet.httpsession.example.mongo.spi.impl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import jp.troter.servlet.httpsession.spi.MongoDBInitializer;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class MongoDBInitializerImpl extends MongoDBInitializer {

    Mongo m = null;

    @Override
    public DB getDB() {
        if (m == null) {
            try {
                m = new Mongo("localhost", 27017);
            } catch (IOException e) {
                return null;
            }
        }
        return m.getDB("httpsession");
    }

    @Override
    public int getSessionTimeout() {
        return Long.valueOf(TimeUnit.HOURS.toSeconds(1L)).intValue();
    }
}
