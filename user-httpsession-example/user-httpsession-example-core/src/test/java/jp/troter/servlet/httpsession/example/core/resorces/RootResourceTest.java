package jp.troter.servlet.httpsession.example.core.resorces;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import jp.troter.servlet.httpsession.UserHttpSessionFilter;
import jp.troter.servlet.httpsession.UserHttpSessionListenerHolder;
import jp.troter.servlet.httpsession.example.core.UserHttpSessionInitializer;
import jp.troter.servlet.httpsession.example.core.resources.RootResource.HttpSessionBindingListenerImpl;

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

    @Test
    public void testHttpSessionListener() throws JSONException {
        HttpSessionListenerImpl impl = new HttpSessionListenerImpl();
        UserHttpSessionListenerHolder.addHttpSessionListener(impl);

        WebResource webResource = resource();
        JSONObject response = null;
        Cookie c = null;

        response = webResource.path("").get(JSONObject.class);
        c = new Cookie("session", response.get("sessionid").toString());
        assertThat(impl.createdCount, is(1));
        assertThat(impl.destroyedCount, is(0));

        webResource.path("").cookie(c).get(JSONObject.class);
        assertThat(impl.createdCount, is(1));
        assertThat(impl.destroyedCount, is(0));

        webResource.path("invalid").cookie(c).get(String.class);
        assertThat(impl.createdCount, is(1));
        assertThat(impl.destroyedCount, is(1));

        response = webResource.path("").get(JSONObject.class);
        c = new Cookie("session", response.get("sessionid").toString());
        assertThat(impl.createdCount, is(2));
        assertThat(impl.destroyedCount, is(1));

        webResource.path("").cookie(c).get(JSONObject.class);
        assertThat(impl.createdCount, is(2));
        assertThat(impl.destroyedCount, is(1));

        webResource.path("invalid").cookie(c).get(String.class);
        assertThat(impl.createdCount, is(2));
        assertThat(impl.destroyedCount, is(2));
    }

    @Test
    public void testHttpSessionAttributeListener() {
        HttpSessionAttributeListenerImpl impl = new HttpSessionAttributeListenerImpl();
        UserHttpSessionListenerHolder.addHttpSessionAttributeListener(impl);

        WebResource webResource = resource();
        webResource.path("attribute").get(String.class);

        assertThat(impl.added.get(0).getName(), is("jp.troter.servlet.httpsession.spi.impl.DefaultSessionValidator.REMOTE_ADDR"));
        assertThat(impl.added.get(1).getName(), is("jp.troter.servlet.httpsession.spi.impl.DefaultSessionValidator.USER_AGENT"));

        assertThat(impl.added.get(2).getName(), is("one"));
        assertThat(impl.added.get(2).getValue(), is((Object)1));
        assertThat(impl.added.get(3).getName(), is("two"));
        assertThat(impl.added.get(3).getValue(), is((Object)2));
        assertThat(impl.added.get(4).getName(), is("three"));
        assertThat(impl.added.get(4).getValue(), is((Object)3));

        assertThat(impl.removed.get(0).getName(), is("one"));
        assertThat(impl.removed.get(0).getValue(), is((Object)1));
        assertThat(impl.removed.get(1).getName(), is("two"));
        assertThat(impl.removed.get(1).getValue(), is((Object)2));

        assertThat(impl.replaced.get(0).getName(), is("three"));
        assertThat(impl.replaced.get(0).getValue(), is((Object)3));
        assertThat(impl.replaced.get(1).getName(), is("three"));
        assertThat(impl.replaced.get(1).getValue(), is((Object)"3"));
    }

    @Test
    public void testHttpSessionBoundListener() {
        HttpSessionBindingListenerImpl impl = HttpSessionBindingListenerImpl.getInstance();

        WebResource webResource = resource();
        webResource.path("bound").get(String.class);

        assertThat(impl.bounds.get(0).event.getName(), is("bound"));
        assertThat(impl.bounds.get(0).event.getValue(), is(nullValue()));
        assertThat(impl.bounds.get(0).count, is(0));

        assertThat(impl.unBounds.get(0).event.getName(), is("bound"));
        assertThat(impl.unBounds.get(0).event.getValue(), is((Object)1));
        assertThat(impl.unBounds.get(0).count, is(1));

        assertThat(impl.bounds.get(1).event.getName(), is("bound"));
        assertThat(impl.bounds.get(1).event.getValue(), is((Object)1));
        assertThat(impl.bounds.get(1).count, is(2));

        assertThat(impl.unBounds.get(1).event.getName(), is("bound"));
        assertThat(impl.unBounds.get(1).event.getValue(), is(nullValue()));
        assertThat(impl.unBounds.get(1).count, is(3));
    }

    public static class HttpSessionListenerImpl implements HttpSessionListener {

        public int createdCount = 0;
        public int destroyedCount = 0;

        @Override
        public void sessionCreated(HttpSessionEvent se) {
            createdCount++;
        }

        @Override
        public void sessionDestroyed(HttpSessionEvent se) {
            destroyedCount++;
        }
    }

    public static class HttpSessionAttributeListenerImpl implements HttpSessionAttributeListener {
        public List<HttpSessionBindingEvent> added = new ArrayList<HttpSessionBindingEvent>();
        public List<HttpSessionBindingEvent> removed = new ArrayList<HttpSessionBindingEvent>();
        public List<HttpSessionBindingEvent> replaced = new ArrayList<HttpSessionBindingEvent>();

        @Override
        public void attributeAdded(HttpSessionBindingEvent event) {
            added.add(event);
        }

        @Override
        public void attributeRemoved(HttpSessionBindingEvent event) {
            removed.add(event);
        }

        @Override
        public void attributeReplaced(HttpSessionBindingEvent event) {
            replaced.add(event);
        }
    }
}
