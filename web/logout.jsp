<%@ page import="pics.PicSession" %><%
  PicSession picSession = PicSession.getSession(pageContext);
  picSession.logout();
  picSession.putMessage("You have been logged out");
%><jsp:include page="login.jsp" /> 
