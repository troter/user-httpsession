package jp.troter.servlet.httpsession.example.memcached.resources;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
}
