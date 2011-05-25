<%@page contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%
if (session.getAttribute("count") == null) {
    session.setAttribute("count", Integer.valueOf(0));
} else {
    out.println("before" + session.getAttribute("count"));
    session.setAttribute("count", Integer.valueOf((Integer)session.getAttribute("count")).intValue() + 1);
}
out.println("after" + session.getAttribute("count"));
%>
