package jp.troter.servlet.httpsession.example.redis.resources;

import static org.junit.Assert.*;

import javax.ws.rs.core.Cookie;

import jp.troter.servlet.httpsession.UserHttpSessionFilter;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

public class RootResourceTest extends JerseyTest {

    public RootResourceTest()throws Exception {
        super(new WebAppDescriptor.Builder("jp.troter.servlet.httpsession.example.redis.resources")
            .servletClass(ServletContainer.class)
            .initParam("com.sun.jersey.api.json.POJOMappingFeature", "true")
            .addFilter(UserHttpSessionFilter.class, "user httpsession filter")
            .build());
    }

    @Test
    public void testRoot() throws JSONException {
        WebResource webResource = resource();
        JSONObject response1 = webResource.path("").get(JSONObject.class);
        Cookie c = new Cookie("SESSIONID", response1.get("sessionid").toString());
        assertEquals("0", response1.get("after").toString());
        assertEquals("true", response1.get("isNew").toString());

        JSONObject response2 = webResource.path("").cookie(c).get(JSONObject.class);
        assertEquals("0", response2.get("before").toString());
        assertEquals("1", response2.get("after").toString());
        assertEquals("false", response2.get("isNew").toString());

        JSONObject response3 = webResource.path("").cookie(c).get(JSONObject.class);
        assertEquals("1", response3.get("before").toString());
        assertEquals("2", response3.get("after").toString());
        assertEquals("false", response3.get("isNew").toString());

        webResource.path("invalid").cookie(c).get(String.class);

        JSONObject response4 = webResource.path("").cookie(c).get(JSONObject.class);
        assertEquals("0", response4.get("after").toString());
        assertEquals("true", response4.get("isNew").toString());
    }
}
