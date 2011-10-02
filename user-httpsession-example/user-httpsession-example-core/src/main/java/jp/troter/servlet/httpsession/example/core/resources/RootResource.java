package jp.troter.servlet.httpsession.example.core.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class RootResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response root(@Context HttpServletRequest req) {
        HttpSession session = req.getSession();
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("sessionid", session.getId());
        if (session.getAttribute("count") == null) {
            session.setAttribute("count", Integer.valueOf(0));
        } else {
            result.put("before", session.getAttribute("count"));
            session.setAttribute("count", Integer.valueOf((Integer)session.getAttribute("count")).intValue() + 1);
        }
        result.put("after", session.getAttribute("count"));
        result.put("isNew", session.isNew());
        result.put("creationTime", session.getCreationTime());
        result.put("lastAccessedTime", session.getLastAccessedTime());
        result.put("maxInactiveInterval", session.getMaxInactiveInterval());
        return Response.ok(result).build();
    }

    @Path("invalid")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String invalid(@Context HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.invalidate();
        return "session invalidate.";
    }

    @Path("attribute")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String attribute(@Context HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.setAttribute("one", 1);
        session.setAttribute("two", 2);
        session.setAttribute("three", 3);

        session.setAttribute("one", null);
        session.removeAttribute("two");
        session.setAttribute("three", "3");
        session.setAttribute("three", 3);

        return "";
    }

    @Path("bound")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String bound(@Context HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.setAttribute("bound", HttpSessionBindingListenerImpl.instance);
        session.setAttribute("bound", 1);
        session.setAttribute("bound", HttpSessionBindingListenerImpl.instance);
        session.removeAttribute("bound");
        return "";
    }

    public static class HttpSessionBindingListenerImpl implements HttpSessionBindingListener {

        public static HttpSessionBindingListenerImpl instance = new HttpSessionBindingListenerImpl();

        public List<Tuple> bounds = new ArrayList<Tuple>();
        public List<Tuple> unBounds = new ArrayList<Tuple>();
        public int count = 0;

        @Override
        public void valueBound(HttpSessionBindingEvent event) {
            bounds.add(new Tuple(count, event));
            count++;
        }

        @Override
        public void valueUnbound(HttpSessionBindingEvent event) {
            unBounds.add(new Tuple(count, event));
            count++;
        }

        public static class Tuple {
            public int count;
            public HttpSessionBindingEvent event;
            public Tuple(int count, HttpSessionBindingEvent event) {
                this.count = count;
                this.event = event;
            }
        }
    }
}
