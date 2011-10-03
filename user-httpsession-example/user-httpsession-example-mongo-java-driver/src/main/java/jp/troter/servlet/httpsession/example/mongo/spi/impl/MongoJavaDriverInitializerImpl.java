package jp.troter.servlet.httpsession.example.mongo.spi.impl;

import java.io.IOException;

import jp.troter.servlet.httpsession.spi.MongoJavaDriverInitializer;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

public class MongoJavaDriverInitializerImpl extends MongoJavaDriverInitializer {

    Mongo m = null;

    @Override
    public DB getDB() {
        if (m == null) {
            try {
                MongoURI uri = new MongoURI("mongodb://127.0.0.1:27017");
                m = new Mongo(uri);
            } catch (IOException e) {
                return null;
            }
        }
        return m.getDB("httpsession");
    }
}
