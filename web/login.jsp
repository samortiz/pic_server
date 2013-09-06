<%@ page import=" util.*,
                  java.util.*,
                  pics.*" %>
<%
 PicSession picSession = PicSession.getSession(pageContext);
 out.write(PicTemplate.header(picSession));

 String username = Filters.escapeDbStr(request.getParameter("username"));
 String password = Filters.escapeDbStr(request.getParameter("password"));
 String mode = request.getParameter("mode");

 StringBuffer message = new StringBuffer();
  message.append(picSession.displayMessagesHTML());

 if ((mode != null) && mode.equals("login")) {
   if (picSession.login(username, password)) {
   // On succesful Login, Go the Main page
     picSession.putHashMap("last_page", null); // clear any previous values
    %><jsp:forward page="/mainpage.jsp"/><%
   } else {
    message.append("<FONT COLOR=RED>Login Failed!</FONT>");
   }
 }
%>

<% // Detect the domain/host
 if (request.getRequestURL().toString().indexOf("churchincalgary.org") >= 0) { %>
 <b><font size='+1'>Sam and Joyce's pictures have moved to a new domain.</font></b><br>
 <br>
 The new domain is <A href="http://www.pongbee.com">http://www.pongbee.com</A><br>
 <br>
 If your DNS has not yet updated (pongbee.com doesn't work for you),<br>
  you can continue using the old domain by logging in below.<br>
<br>
<% } %>


<FORM NAME="loginform" METHOD=POST ACTION="/login.jsp?mode=login">
<TABLE BORDER=0 CELLSPACING=1 CELLPADDING=3 BGCOLOR=BLACK ><!-- Black border table -->
<TR><TD BGCOLOR="<%=PicConst.HIGHLIGHT%>" colspan='100%'>
     <TABLE BORDER='0' CELLSPACING='0' CELLPADDING='0' width='100%'><TR>
       <TD align=left><FONT SIZE="+1"><B>Login</B></FONT></TD>
       <TD align=right><A HREF="/adduser.jsp"><B>New User</B></A></TD>
     </TR></TABLE>
</TD></TR>


 <TR BGCOLOR='<%=PicConst.LIGHT%>'><TD>
   <TABLE BORDER=0 BGCOLOR=WHITE CELLSPACING=0 CELLPADDING=3>
     <TR><TD>User Name</TD><TD><INPUT TYPE=TEXT NAME="username" value="<%=username%>" SIZE=20 MAXLENGTH=30></TD></TR>
     <TR><TD>Password</TD><TD><INPUT TYPE=PASSWORD NAME="password" value="" SIZE=20 MAXLENGTH=30></TD></TR>
     <TR><TD colspan='100%' align=center><INPUT TYPE=Submit NAME="submit" VALUE="Submit"></TD></TR>
   </TABLE>
 </TD></TR>

<%
if (message.length() > 0) {
 out.write("<TR BGCOLOR=WHITE><TD>"+message.toString()+"</TD></TR>");
}
%>
</TABLE>
</FORM>

<SCRIPT>
  document.loginform.username.focus();
</SCRIPT>

<%=PicTemplate.footer(picSession)%>
