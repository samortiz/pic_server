<%@ page import="java.util.*,
                 pics.* "%>
<%@ include file="picauth.jsp"%>
<%
  String lastPage = (String) picSession.getFromHashMap("last_page");
  if (lastPage == null) lastPage = "search.jsp";
%>
<jsp:include page="<%=lastPage%>" />

