package jp.troter.servlet.httpsession.spi.impl;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jp.troter.servlet.httpsession.spi.MongoDBInitializer;
import jp.troter.servlet.httpsession.spi.SessionStateManager;
import jp.troter.servlet.httpsession.spi.SessionValueSerializer;
import jp.troter.servlet.httpsession.state.DefaultSessionState;
import jp.troter.servlet.httpsession.state.SessionState;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoSessionStateManager extends SessionStateManager {

    private static Logger log = LoggerFactory.getLogger(MongoSessionStateManager.class);

    private static final String ATTRIBUTES_KEY = "attributes";

    private static final String LAST_ACCESSED_TIME_KEY = "last_accessed_time";

    private static final String MAX_INACTIVE_INTERVAL_KEY = "max_inactive_interval";

    protected MongoDBInitializer initializer;

    protected SessionValueSerializer serializer;

    @Override
    public SessionState loadState(String sessionId) {
        DBObject query = new BasicDBObject();
        query.put("session_id", sessionId);
        DBCursor cursor = getSessionCollection().find(query);
        try {
            while(cursor.hasNext()) {
                return restoredSessionState(cursor.next());
            }
        } catch (RuntimeException e) {
            log.warn("MongoDB exception occurred at find method. session_id=" + sessionId, e);
            removeState(sessionId);
        }

        return new DefaultSessionState(getDefaultTimeoutSecond());
    }

    @Override
    public void saveState(String sessionId, SessionState sessionState) {
        removeState(sessionId);
        DBObject session = storedSessionState(sessionState);
        session.put("session_id", sessionId);
        try {
            getSessionCollection().save(session);
        } catch (RuntimeException e) {
            log.warn("MongoDB exception occurred at insert method. session_id=" + sessionId, e);
        }
    }

    @Override
    public void removeState(String sessionId) {
        DBObject query = new BasicDBObject();
        query.put("session_id", sessionId);
        DBCursor cursor = getSessionCollection().find(query);
        try {
            while (cursor.hasNext()) {
                getSessionCollection().remove(cursor.next());
            }
        } catch (RuntimeException e) {
            log.warn("MongoDB exception occurred at insert method. session_id=" + sessionId, e);
        }
    }

    @Override
    public int getDefaultTimeoutSecond() {
        return getInitializer().getDefaultTimeoutSecond();
    }

    protected SessionState restoredSessionState(DBObject obj) {
        int maxInactiveInterval = ((Integer)obj.get(getMaxInactiveIntervalKey())).intValue();
        long lastAccessedTime = ((Long)obj.get(getLastAccessedTimeKey())).longValue();
        if (lastAccessedTime > getTimeoutTime()) {
            return new DefaultSessionState(getDefaultTimeoutSecond());
        }
        DBObject serializedAttributes = (DBObject) obj.get(getAttributesKey());
        Map<String, Object> attributes = new HashMap<String, Object>();
        for (String name : serializedAttributes.keySet()) {
            try {
                byte[] objectData = (byte[])serializedAttributes.get(name);
                attributes.put(decodeFieldName(name), getSerializer().deserialize(objectData));
            } catch (RuntimeException e) {
                log.warn("undeserialize object name: " + name, e);
            }
        }
        return new DefaultSessionState(attributes, lastAccessedTime, false, maxInactiveInterval);
    }

    protected DBObject storedSessionState(SessionState sessionState) {
        DBObject attributes = new BasicDBObject();
        for (Enumeration<?> e = sessionState.getAttributeNames(); e.hasMoreElements();) {
            String name = (String)e.nextElement();
            Object value = sessionState.getAttribute(name);
            if (value == null) { continue; }
            try {
                attributes.put(encodeFieldName(name), getSerializer().serialize((Serializable)value));
            } catch (RuntimeException x) {
                log.warn("unserialize object name: " + name, x);
            }
        }

        DBObject session = new BasicDBObject();
        session.put(getAttributesKey(), attributes);
        session.put(getLastAccessedTimeKey(), sessionState.getCreationTime());
        session.put(getMaxInactiveIntervalKey(), sessionState.getMaxInactiveInterval());
        return session;
    }

    protected String encodeFieldName(String name) {
        return StringUtils.replace(name, ".", "[dot]");
    }

    protected String decodeFieldName(String name) {
        return StringUtils.replace(name, "[dot]", ".");
    }

    protected DBCollection getSessionCollection() {
        return getInitializer().getDBCollection();
    }

    protected String getAttributesKey() {
        return ATTRIBUTES_KEY;
    }

    protected String getLastAccessedTimeKey() {
        return LAST_ACCESSED_TIME_KEY;
    }

    protected String getMaxInactiveIntervalKey() {
        return MAX_INACTIVE_INTERVAL_KEY;
    }
    protected MongoDBInitializer getInitializer() {
        if (initializer == null) {
            initializer = MongoDBInitializer.newInstance();
        }
        return initializer;
    }

    protected SessionValueSerializer getSerializer() {
        if (serializer == null) {
            serializer = SessionValueSerializer.newInstance();
        }
        return serializer;
    }
}
