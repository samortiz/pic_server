<%@ page import=" util.*,
                  pics.*" %>
<%
 PicSession picSession = PicSession.getSession(pageContext);
 out.write(PicTemplate.header(picSession));
%>

 <b><font size='+1'>Sam and Joyce's pictures have moved to a new domain.</font></b><br>
 <br>
 The new domain is <A href="http://www.pongbee.com">http://www.pongbee.com</A><br>
 <br>
 If your DNS has not yet updated (pongbee.com doesn't work for you), <br>
 you can continue using the old domain by logging in with the below link.<br>

<%=PicTemplate.footer(picSession)%>
