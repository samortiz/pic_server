<%@ page import="pics.PicSession"%><%
  PicSession picSession = PicSession.getSession(pageContext);
  if (!picSession.isLoggedIn() || !picSession.hasPermission("Admin")) {
    picSession.putMessage("You do not have admin permissions.  You cannot use admin functions.");
     %><jsp:forward page="/index.jsp"/><%
  } %>