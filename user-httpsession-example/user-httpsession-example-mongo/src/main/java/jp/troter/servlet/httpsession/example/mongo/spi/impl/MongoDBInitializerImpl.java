package jp.troter.servlet.httpsession.example.mongo.spi.impl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import jp.troter.servlet.httpsession.spi.MongoDBInitializer;

import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

public class MongoDBInitializerImpl extends MongoDBInitializer {

    Mongo m = null;

    @Override
    public DBCollection getDBCollection() {
        if (m == null) {
            try {
                MongoURI uri = new MongoURI("mongodb://127.0.0.1:27017");
                m = new Mongo(uri);
            } catch (IOException e) {
                return null;
            }
        }
        return m.getDB("httpsession").getCollection("httpsession");
    }

    @Override
    public int getSessionTimeout() {
        return Long.valueOf(TimeUnit.HOURS.toSeconds(1L)).intValue();
    }
}
