<%@ page import=" util.*,
                  pics.*,
                  java.util.*,
                  pics.admin.Slurp"
 %><%@ include file="adminauth.jsp"%>
<%=PicTemplate.header(picSession)%>
<%
  // Setup a new Slurper.
  Slurp slurp = new Slurp(picSession);


%>
Slurped <%=slurp.getNewFileCount()%> pictures! </BR>
<BR>

<!-- Show any errors -->
<FONT COLOR='RED'><%=picSession.displayMessagesHTML()%></FONT>

<%=PicTemplate.footer(picSession)%>