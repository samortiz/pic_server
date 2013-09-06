<%@ page import="pics.PicSession"%><%
  PicSession picSession = PicSession.getSession(pageContext);
  if (!picSession.isLoggedIn()) {
     %><jsp:forward page="index.jsp"/><%
  } %>