package jp.troter.servlet.httpsession.example.core.resorces;

import static org.junit.Assert.*;

import java.util.List;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import jp.troter.servlet.httpsession.UserHttpSessionFilter;
import jp.troter.servlet.httpsession.example.core.UserHttpSessionInitializer;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

public class RootResourceTest extends JerseyTest {

    public RootResourceTest()throws Exception {
        super(new WebAppDescriptor.Builder("jp.troter.servlet.httpsession.example.core.resources")
            .servletClass(ServletContainer.class)
            .initParam("com.sun.jersey.api.json.POJOMappingFeature", "true")
            .contextListenerClass(UserHttpSessionInitializer.class)
            .addFilter(UserHttpSessionFilter.class, "user httpsession filter")
            .build());
    }

    @Test
    public void testRoot() throws JSONException {
        WebResource webResource = resource();
        JSONObject response1 = webResource.path("").get(JSONObject.class);
        Cookie c = new Cookie("session", response1.get("sessionid").toString());
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

    @Test
    public void testSessionCookieHandlerSystemProperty() {
        WebResource webResource = resource();
        ClientResponse response = webResource.path("").get(ClientResponse.class);
        List<NewCookie> cookies = response.getCookies();
        boolean hasSessionCookie = false;
        boolean customDomain = false;
        boolean customPath = false;
        boolean secure = false;
        for (NewCookie newCookie : cookies) {
            if (newCookie.getName().equals("session")) {
                hasSessionCookie = true;
                if (newCookie.getDomain().equals("example.com")) {
                    customDomain = true;
                }
                if (newCookie.getPath().equals("/example/")) {
                    customPath = true;
                }
                secure = newCookie.isSecure();
            }
        }
        assertTrue(hasSessionCookie);
        assertTrue(customDomain);
        assertTrue(customPath);
        assertTrue(secure);
    }
}
