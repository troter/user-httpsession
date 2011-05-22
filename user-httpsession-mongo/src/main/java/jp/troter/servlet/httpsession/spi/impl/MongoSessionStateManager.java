package jp.troter.servlet.httpsession.spi.impl;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.spi.SessionValueSerializer;
import jp.troter.servlet.httpsession.spi.MongoDBInitializer;
import jp.troter.servlet.httpsession.state.DefaultSessionState;
import jp.troter.servlet.httpsession.state.SessionState;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoSessionStateManager extends SessionStateManager {

    public static final String SESSION_COLLECTION_NAME = "httpsession";

    public static final String ATTRIBUTES_KEY = "attributes";

    protected MongoDBInitializer initializer;

    protected SessionValueSerializer serializer;

    @Override
    public SessionState loadState(String sessionId) {
        DBObject obj = findBySessionId(sessionId);
        Map<String, Object> attributes = new HashMap<String, Object>();
        if (obj != null) {
            DBObject rawAttributes = (DBObject)obj.get(ATTRIBUTES_KEY);
            for (String key : rawAttributes.keySet()) {
                try {
                    byte[] objectData = (byte[])rawAttributes.get(key);
                    attributes.put(key, getSessionValueSerializer().deserialize(objectData));
                } catch (Exception UserHttpSessionSerializationException) {
                }
            }
        }

        return new DefaultSessionState(attributes);
    }

    @Override
    public void updateState(String sessionId, SessionState sessionState) {
        BasicDBObject attributes = new BasicDBObject();

        for (Enumeration<?> e = sessionState.getAttributeNames(); e.hasMoreElements();) {
            String name = (String)e.nextElement();
            Object value = sessionState.getAttribute(name);
            if (value == null) { continue; }
            try {
                attributes.put(name, getSessionValueSerializer().serialize((Serializable)value));
            } catch (Exception UserHttpSessionSerializationException) {
            }
        }

        BasicDBObject session = new BasicDBObject();
        session.put("_id", sessionId);
        session.put(ATTRIBUTES_KEY, attributes);

        DBCollection coll = getSessionCollection();
        coll.insert(session);
    }

    @Override
    public void removeState(String sessionId) {
        DBCollection coll = getSessionCollection();
        DBObject obj = findBySessionId(sessionId);
        if (obj != null) {
            coll.remove(obj);
        }
    }

    protected DBObject findBySessionId(String sessionId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", sessionId);
        DBCollection coll = getSessionCollection();
        DBCursor cur = coll.find(query);
        while(cur.hasNext()) {
            return cur.next();
        }
        return null;
    }

    protected DBCollection getSessionCollection() {
        DB db = initializer.getDB();
        return db.getCollection(SESSION_COLLECTION_NAME);
    }

    protected MongoDBInitializer getMongoDBInitializer() {
        if (initializer == null) {
            initializer = MongoDBInitializer.newInstance();
        }
        return initializer;
    }

    protected SessionValueSerializer getSessionValueSerializer() {
        if (serializer == null) {
            serializer = SessionValueSerializer.newInstance();
        }
        return serializer;
    }
}
