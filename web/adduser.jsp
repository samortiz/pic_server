<%@ page import="pics.*,
                 util.*,
                 org.postgresql.util.UnixCrypt"%>
<% PicSession picSession = PicSession.getSession(pageContext); %>
<%=PicTemplate.header(picSession)%>
<%
  boolean isAdmin = picSession.hasPermission("Admin");

  String mode = request.getParameter("mode");
  String name = Filters.escapeDbStr(request.getParameter("name"));
  String username = Filters.escapeDbStr(request.getParameter("username"));
  String password = Filters.escapeDbStr(request.getParameter("password"));
  String password2 = Filters.escapeDbStr(request.getParameter("password2"));
  String question = Filters.escapeDbStr(request.getParameter("question"));
  String permissions = Filters.escapeDbStr(request.getParameter("permissions"));

  StringBuffer messages = new StringBuffer();
  if ("newuser".equals(mode)) {

    if ((messages.length() == 0) && !password.equals(password2)) {
      messages.append("<FONT COLOR='RED'>Passwords do not match!</FONT>");
      password = "";
      password2 = "";
    }

    if ((messages.length() == 0) && (password.length() == 0)) {
      messages.append("<FONT COLOR='RED'>You must supply a password!</FONT>");
    }

    if ((messages.length() == 0) && (name.length() == 0)) {
      messages.append("<FONT COLOR='RED'>Name cannot be blank!</FONT>");
    }

    if ((messages.length() == 0) && (username.length() == 0)) {
      messages.append("<FONT COLOR='RED'>User Name cannot be blank!</FONT>");
    }

    if ((messages.length() == 0) && (question.toLowerCase().indexOf("spirit") < 0 )) {
      messages.append("<FONT COLOR='RED'>You answered the knowledge testing question wrong!</FONT>");
    }

    if ( (messages.length() == 0) &&
      PicDb.getSingleValue("select uid from users where lower(username)=lower('"+username+"')").length() > 0) {
        messages.append("<FONT COLOR='RED'>User Name is already taken!</FONT>");
    }

    // Only administrators can set permissions
    if (!isAdmin) permissions = "";

    // Everything is good.  Create the user
    if (messages.length() == 0) {
      String hashPwd = UnixCrypt.crypt(password);
      PicDb.update("insert into users (username, password, name, permissions) values "+
          " (lower('"+username+"'), '"+hashPwd+"', '"+name+"', '"+permissions+"')");
      if (!isAdmin) {
        picSession.login(username, password);
        %><jsp:forward page="/mainpage.jsp"/><%
      }
    }


  } // newuser mode

  // Adding a new user

%>

<TABLE BORDER=1 class="outer_border" CELLSPACING=0 CELLPADDING=3 BGCOLOR='<%=PicConst.LIGHT%>'>
 <FORM METHOD="post" ACTION="/adduser.jsp?mode=newuser">
 <TR><TD colspan='100%' BGCOLOR="<%=PicConst.HIGHLIGHT%>" align='CENTER' ><B>Create a New User</B></TD></TR>

<% if (messages.length() > 0) { %>
  <TR><TD COLSPAN='100%' align='left'><%=messages.toString()%></TD></TR>
<% } %>

 <TR><TD>Display Name</TD><TD><INPUT TYPE=text name="name" value="<%=name%>"></TD></TR>
 <TR><TD>Login Name</TD><TD><INPUT TYPE=text name="username" value="<%=username%>"></TD></TR>
 <TR><TD>Password</TD><TD><INPUT TYPE=password name="password" value="<%=password%>"></TD></TR>
 <TR><TD>Password (again) </TD><TD><INPUT TYPE=password name="password2" value="<%=password2%>"></TD></TR>
 <TR><TD colspan='100%' valign='center' BGCOLOR='WHITE'>
   <TABLE BORDER='0' CELLSPACING='0' CELLPADDING='0'>
    <TR><TD colspan='100%' align='center'>Knowledge Testing Question</TD></TR>
    <TR>
     <TD valign='center' align='right'><IMG SRC="/images/circles.gif"></TD>
     <TD valign='center' align='left'><INPUT TYPE=text name="question" size='8' value="<%=question%>"></TD>
   </TR></TABLE>
 </TD></TR>
<% if (isAdmin) { %>
  <TR><TD>Permissions</TD><TD><INPUT TYPE=text name="permissions" value="<%=permissions%>"></TD></TR>
<% } %>

 <TR><TD colspan='100%' align='center'><INPUT TYPE='Submit' VALUE='Add User'></TD></TR>

 </FORM>
</TABLE>

<%=PicTemplate.footer(picSession)%>
