<%@ page import="java.util.*,
                 pics.* "%>
<%@ include file="adminauth.jsp"%>
<%=PicTemplate.header(picSession)%>
<%
  picSession.putHashMap("last_page", "/admin/admin.jsp");
%>

<TABLE class='outer_border' CELLSPACING=0 CELLPADDING=3 BGCOLOR='<%=PicConst.LIGHT%>'>

 <%=PicTemplate.navRow(PicTemplate.ADMIN, picSession)%>

 <TR><TD colspan='100%'> <A HREF="/admin/slurp.jsp"> Slurp </A></TD></TR>

 <!-- <TR><TD colspan='100%'> <A HREF="/admin/import.jsp"> Import </A></TD></TR> -->

 <TR><TD colspan='100%'> <A HREF="/admin/editevents.jsp"> Edit Events</A></TD></TR>

 <TR><TD colspan='100%'> <A HREF="/admin/make_screensaver_images.jsp"> Update Sam's Screesaver (Takes a very long time!) </A></TD></TR>

</TABLE>

<%=PicTemplate.footer(picSession)%>
