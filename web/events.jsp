<%@ page import="java.util.*,
                 pics.*,
                 java.text.SimpleDateFormat,
                 util.*"%>
<%@ include file="picauth.jsp"%>
<%=PicTemplate.header(picSession)%>
<%
  boolean isAdmin = picSession.hasPermission("Admin");
  picSession.putHashMap("last_page", "/events.jsp");

%>
<TABLE BORDER=0 CELLSPACING=0 CELLPADDING=3 BGCOLOR='<%=PicConst.LIGHT%>' class='outer_border'>

<%=PicTemplate.navRow(PicTemplate.EVENT, picSession)%>

 <TR><TD colspan='100%' align='center'>
  <TABLE BORDER='0' CELLSPACING='0' CELLPADDING='3' width='100%' >
<%
  ArrayList events = PicDb.getManyRows("select * from pic_event order by eorder desc ");
  for (int i=0; i<events.size(); i++) {
    HashMap event = (HashMap) events.get(i);
    String name = (String) event.get("name");
    String params = (String) event.get("params");
    out.write("<TR>");
    out.write("<TD><A HREF='/viewthumbs.jsp?newview=true&"+params+"'>"+name+"</A></TD>");
    out.write("</TR>");
  }

if (isAdmin) out.write("<TR><TD align='center'><B><A HREF='/admin/editevents.jsp'> Edit Events </A></B></TD></TR>");
%>
   </TABLE>

  </TD></TR>
</TABLE>

<%=PicTemplate.footer(picSession)%>

