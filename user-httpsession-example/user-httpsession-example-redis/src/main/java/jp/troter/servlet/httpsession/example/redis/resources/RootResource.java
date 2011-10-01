package jp.troter.servlet.httpsession.example.redis.resources;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RootResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String root(@Context HttpServletRequest req) {
        HttpSession session = req.getSession();
        StringBuilder c = new StringBuilder();
        if (session.getAttribute("count") == null) {
            session.setAttribute("count", Integer.valueOf(0));
        } else {
            c.append("before " + session.getAttribute("count")).append("\n");
            session.setAttribute("count", Integer.valueOf((Integer)session.getAttribute("count")).intValue() + 1);
        }
        c.append("after  " + session.getAttribute("count")).append("\n");
        c.append("creationTime     " + session.getCreationTime()).append("\n");
        c.append("creationTime     " + session.getCreationTime()).append("\n");
        return c.toString();
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
