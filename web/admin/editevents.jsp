<%@ page import="java.util.*,
                 pics.*,
                 util.*"%>
<%@ include file="adminauth.jsp"%>
<%=PicTemplate.header(picSession)%>
<%
  String mode = request.getParameter("mode");

  String peid = Filters.escapeDbStr(request.getParameter("peid"));
  String eorder = Filters.escapeDbStr(request.getParameter("eorder"));
  String name = Filters.escapeDbStr(request.getParameter("name"));
  String start = DateUtils.getDateAsString(request, "start");
  String end = DateUtils.getDateAsString(request, "end");
  boolean noDate = (request.getParameter("nodate") != null) && request.getParameter("nodate").equals("true");
  String keyword = Filters.escapeDbStr(request.getParameter("keyword"));
  String filter = Filters.escapeDbStr(request.getParameter("filter"));
  String pids = Filters.escapeDbStr(request.getParameter("pids"));
  String filterPids = Filters.escapeDbStr(request.getParameter("filterpids"));
  boolean unnamed = (request.getParameter("unnamed") != null) && request.getParameter("unnamed").equals("true");
  boolean movie = (request.getParameter("movie") != null) && request.getParameter("movie").equals("true");
  boolean hidden = (request.getParameter("hidden") != null) && request.getParameter("hidden").equals("true");

  String params = "start_field="+start + "&end_field="+end + (noDate ? "&nodate=true" : "") +
      "&keyword="+keyword+"&filter="+filter+"&pids="+pids+"&filterpids="+filterPids + (unnamed ? "&unnamed=true" : "") +
      (movie ? "&movie=true" : "") + (hidden ? "&hidden=true" : "");
      
  if ("saveedit".equals(mode) || "saveadd".equals(mode)) {
    // Updating
    if ("saveedit".equals(mode)) {
      PicDb.update("update pic_event set name='"+name+"', params='"+params+"', eorder="+eorder+" where peid="+peid);
    // Inserting
    } else if ("saveadd".equals(mode)) {
      PicDb.update("insert into pic_event (name, params, eorder) values ('"+name+"', '"+params+"', "+eorder+") ");
    }

  } else if ("delete".equals(mode)) {
    PicDb.update("delete from pic_event where peid = "+peid);
  }



%>
<!-- For using the date chooser dialog -->
<%=DateUtils.getJavaScriptHeader()%>
<TABLE BORDER=0 CELLSPACING=0 CELLPADDING=3 BGCOLOR='<%=PicConst.LIGHT%>' class='outer_border'>

 <TR><TD colspan='100%' align='center'>
  <TABLE BORDER=0 CELLSPACING=3 CELLPADDING=5 width='100%' >
   <TR><TD align='center'><B>Edit Events</B></TD></TR>
<%
  if ("edit".equals(mode) || "add".equals(mode)) {
    String thisPeid = Filters.escapeDbStr(request.getParameter("peid"));
    if ((peid == null) || peid.length() == 0) peid = "-1";
    HashMap thisEvent = PicDb.getRow("select * from pic_event where peid="+peid);
    String thisName = (String) thisEvent.get("name");
    String thisEorder = (String) thisEvent.get("eorder");
    String thisParams = (String) thisEvent.get("params");
    HashMap thisParam = MyUtils.loadParams(thisParams);
    String thisStart = (String) thisParam.get("start_field");
    String thisEnd = (String) thisParam.get("end_field");
    boolean thisNoDate = (thisParam.get("nodate") != null) && thisParam.get("nodate").equals("true");
    String thisKeyword = (String) thisParam.get("keyword");
    if (thisKeyword == null) thisKeyword = "";
    String thisFilter = (String) thisParam.get("filter");
    if (thisFilter == null) thisFilter = "";
    String thisPids = (String) thisParam.get("pids");
    if (thisPids == null) thisPids = "";
    String thisFilterPids = (String) thisParam.get("filterpids");
    if (thisFilterPids == null) thisFilterPids = "";
    boolean thisUnnamed = (thisParam.get("unnamed") != null) && thisParam.get("unnamed").equals("true");
    boolean thisMovie = (thisParam.get("movie") != null) && thisParam.get("movie").equals("true");
    boolean thisHidden = (thisParam.get("hidden") != null) && thisParam.get("hidden").equals("true");

    if ("add".equals(mode)) {
      thisPeid = "-1";
      thisEorder = PicDb.getSingleValue("select max(eorder)+1 from pic_event");
      thisName = "";
      thisStart = null;
      thisEnd = null;
      noDate = false;
      thisKeyword = "";
      thisFilter = "";
      thisPids = "";
      thisFilterPids = "";
    }
%>
  <FORM METHOD="post" ACTION="/admin/editevents.jsp?mode=save<%=mode%>">
   <INPUT TYPE="hidden" NAME="peid" value="<%=thisPeid%>">

    <TR><TD>Order</TD><TD><INPUT TYPE="text" name="eorder" value="<%=thisEorder%>"></TD></TR>
    <TR><TD>Name</TD><TD><INPUT TYPE="text" name="name" value="<%=thisName%>"></TD></TR>

    <TR><TD valign='top'>Date</TD><TD>
      From <%=DateUtils.getJavaScript(thisStart, "start")%> To <%=DateUtils.getJavaScript(thisEnd, "end")%><BR>
      <INPUT TYPE="checkbox" name="nodate" value="true" <%=(thisNoDate ? " CHECKED " : "")%>> No Date Limitation
    </TD></TR>

    <TR><TD>Containing</TD><TD><INPUT TYPE=text name="keyword" value="<%=thisKeyword%>" size='30'></TD></TR>
    <TR><TD>Filter</TD><TD><INPUT TYPE=text name="filter" value="<%=thisFilter%>" size='30'></TD></TR>
    <TR><TD>PID #</TD><TD><INPUT TYPE=text name="pids" value="<%=thisPids%>" size='30'></TD></TR>
    <TR><TD>FilterPID</TD><TD><INPUT TYPE=text name='filterpids' value='<%=thisFilterPids%>' size='30'></TD></TR>

    <TR><TD valign='top'>Options</TD><TD>
      <INPUT TYPE="checkbox" name="unnamed" value="true" <%=(thisUnnamed ? " CHECKED " : "")%>> Un-Named Pictures Only <BR>
      <INPUT TYPE='checkbox' name='movie' value='true' <%=(thisMovie ? " CHECKED " : "")%>> Include Videos<BR>
      <INPUT TYPE='checkbox' name='hidden' value='true' <%=(thisHidden ? " CHECKED " : "")%>> Include Hidden<BR>
    </TD></TR>

    <TR><TD colspan='100%' align='center'><INPUT TYPE="Submit" value="Submit"></TD></TR>
  </FORM>
<%

    // Not editing, doing regular display
  } else {
    ArrayList events = PicDb.getManyRows(
        "select * from pic_event order by eorder desc");
    for (int i=0; i<events.size(); i++) {
      HashMap event = (HashMap) events.get(i);
      String thisPeid = (String) event.get("peid");
      String thisEorder = (String) event.get("eorder");
      String thisName = (String) event.get("name");

      out.write("<TR>");
      out.write("<TD><A HREF='/admin/editevents.jsp?mode=edit&peid="+thisPeid+"'>"+thisEorder+" "+thisName+"</A> - " +
          "<A HREF='/admin/editevents.jsp?mode=delete&peid="+thisPeid+"'><IMG SRC='/images/delete16.gif' BORDER=0></A></TD>");
      out.write("</TR>");
    }
  } // else edit != mode

%>
   <TR><TD colspan='100%'>
      <A HREF="/admin/editevents.jsp?mode=add">Add New Event</A> &nbsp; &nbsp;
      <A HREF="/admin/editevents.jsp">View Events</A>
   </TD></TR>
   </TABLE>

  </TD></TR>
</TABLE>

<%=PicTemplate.footer(picSession)%>

