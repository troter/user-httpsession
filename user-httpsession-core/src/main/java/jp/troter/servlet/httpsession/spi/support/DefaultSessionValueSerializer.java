package jp.troter.servlet.httpsession.spi.support;

import java.io.Serializable;

import jp.troter.servlet.httpsession.exception.UserHttpSessionSerializationException;
import jp.troter.servlet.httpsession.spi.SessionValueSerializer;

import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;

public class DefaultSessionValueSerializer extends SessionValueSerializer {

    @Override
    public Object deserialize(byte[] objectData)
            throws UserHttpSessionSerializationException, IllegalArgumentException {
        if (objectData == null) { throw new IllegalArgumentException(); }
        try {
            return SerializationUtils.deserialize(objectData);
        } catch (SerializationException e) {
            throw new UserHttpSessionSerializationException();
        }
    }

    @Override
    public byte[] serialize(Serializable obj)
            throws UserHttpSessionSerializationException {
        try {
            return SerializationUtils.serialize(obj);
        } catch (SerializationException e) {
            throw new UserHttpSessionSerializationException();
        }
    }

}
