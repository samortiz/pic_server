<%@ page import="java.util.*,
                 pics.*,
                 util.DateUtils"%>
<%@ include file="picauth.jsp"%>
<%=PicTemplate.header(picSession)%>
<%
  Calendar defaultStart = Calendar.getInstance();
  defaultStart.add(Calendar.DAY_OF_YEAR, -7);
  boolean isAdmin = picSession.hasPermission("Admin");

  picSession.putHashMap("last_page", "/search.jsp");

%>

<!-- For using the date chooser dialog -->
<%=DateUtils.getJavaScriptHeader()%>

<TABLE class='outer_border' CELLSPACING=0 CELLPADDING=3 BGCOLOR='<%=PicConst.LIGHT%>'>
 <FORM METHOD="post" ACTION="/viewthumbs.jsp?newview=true">

 <%=PicTemplate.navRow(PicTemplate.SEARCH, picSession)%>

 <TR><TD colspan='100%'>
 <TABLE BORDER=0 CELLSPACING=0 CELLPADDING=3>

 <TR><TD valign='top'>Date</TD><TD>
    From <%=DateUtils.getJavaScript(defaultStart, "start")%> To <%=DateUtils.getJavaScript("end")%><BR>
    <INPUT TYPE="checkbox" name="nodate" value="true" CHECKED> No Date Limitation
 </TD></TR>

 <TR><TD>Containing</TD><TD><INPUT TYPE=text name="keyword" value="" size='30'></TD></TR>
 <TR><TD>Remove</TD><TD><INPUT TYPE=text name="filter" value="" size='30'></TD></TR>
 <TR><TD>PID #</TD><TD><INPUT TYPE=text name="pids" value="" size='30'></TD></TR>
 <%=(isAdmin ? "<TR><TD>FilterPID</TD><TD><INPUT TYPE=text name='filterpids' value='' size='30'></TD></TR>" : "")%>

 <TR><TD valign='top'>Options</TD><TD>
    <INPUT TYPE="checkbox" name="unnamed" value="true"> Un-Named Pictures Only <BR>
   <%=(isAdmin ? "<INPUT TYPE='checkbox' name='movie' value='true'> Include Videos<BR>" : "")%>
   <%=(isAdmin ? "<INPUT TYPE='checkbox' name='hidden' value='true'> Include Hidden<BR>" : "")%>
   <%=(isAdmin ? "<INPUT TYPE='checkbox' name='no_vertical' value='true' > No Vertical <BR>" : "")%>
   <%=(isAdmin ? "<INPUT TYPE='checkbox' name='no_horizontal' value='true' > No Horizontal<BR>" : "")%>
 </TD></TR>

 <TR><TD colspan='100%' align='center'><INPUT TYPE='Submit' VALUE='View Pictures'></TD></TR>

 </TABLE>

</FORM>
</TABLE>

<%=PicTemplate.footer(picSession)%>
