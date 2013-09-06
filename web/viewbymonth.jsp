<%@ page import="java.util.*,
                 pics.*,
                 java.text.SimpleDateFormat,
                 util.*"%>
<%@ include file="picauth.jsp"%>
<%=PicTemplate.header(picSession)%>
<%
  int NUM_COLS = 6;
  //boolean isAdmin = picSession.hasPermission("Admin");

  SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
  SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yy");
  SimpleDateFormat dayFormat = new SimpleDateFormat("MMM dd");

  String mode = request.getParameter("mode");
  if (mode == null) mode = "month";

  String month = request.getParameter("month");

  Calendar current = Calendar.getInstance();
  Date start;
  Date end;
  int step;

  if ("month".equals(mode)) {
    start = DateUtils.stringToDate(PicDb.getSingleValue("select min(created) from pic"));
    end = DateUtils.stringToDate(PicDb.getSingleValue("select max(created) from pic"));
    step = Calendar.MONTH;
    // Set the start day to the first of the month (so that end time is always greater than it!)
    Calendar temp  = Calendar.getInstance();
    temp.setTime(start);
    temp.set(Calendar.DAY_OF_MONTH, 1);
    start.setTime(temp.getTime().getTime());

  } else if ("day".equals(mode)) {
    start = monthFormat.parse(month);
    current.setTime(start);
    current.add(Calendar.MONTH, 1);
    end = current.getTime();
    // Current will be reset to start later
    step = Calendar.DAY_OF_YEAR;

  } else {
    throw new MyException("Mode "+mode+" is not known!");
  }

  // Set (or reset) the current date to the starting date
  current.setTime(start);

  picSession.putHashMap("last_page", "/viewbymonth.jsp?mode="+mode+"&month="+month);


%>

<TABLE BORDER=0 CELLSPACING=0 CELLPADDING=3 BGCOLOR='<%=PicConst.LIGHT%>' class='outer_border'>
 <FORM METHOD="post" NAME="viewform" ACTION="/viewthumbs.jsp?newview=true">
 <INPUT TYPE="hidden" NAME="start_field" value="">
 <INPUT TYPE="hidden" NAME="end_field" value="">
  <INPUT TYPE="hidden" NAME="keyword" value="">
  <INPUT TYPE="hidden" NAME="filter" value="">

 <%=PicTemplate.navRow(PicTemplate.DATE, picSession)%>  

 <TR><TD colspan='100%' align='center'>
  <TABLE BORDER=0 CELLSPACING=3 CELLPADDING=5 width='100%' >
   <TR>
<%

  int i = 0;
  if ("day".equals(mode)) {
     out.write("<TR><TD valign='center' align='center'><A HREF='/viewbymonth.jsp'>" +
         "<B>"+yearFormat.format(current.getTime())+"</B></A></TD>");
    i++;
   }

  while (current.getTime().getTime() < end.getTime()) {
    // Starting a new Row
    if (i % NUM_COLS == 0) {
      if (i > 0) out.write("</TR>");
      out.write("<TR>");
    }

    // Month Mode
    if ("month".equals(mode)) {
      String thisMonth = monthFormat.format(current.getTime());
      out.write("<TD valign='center' align='center'><A HREF='/viewbymonth.jsp?mode=day&month="+Filters.escapeURL(thisMonth)+"'>");
      out.write(thisMonth+"</A></TD>");
      i++;

    // Day Mode
    } else if ("day".equals(mode)) {
      String dayCount = PicDb.getSingleValue("select count(*) from pic where hidden=false and movie=false and " +
          " created::date = '"+DateUtils.dateToString(current.getTime())+"'::date ");
      if (!dayCount.equals("0")) {
        String thisDay = dayFormat.format(current.getTime());
        String dateField = DateUtils.dateToString(current.getTime());
        out.write("<TD valign='center' align='center'><A HREF='/viewthumbs.jsp?newview=true&" +
            "start_field="+Filters.escapeURL(dateField)+"&end_field="+dateField+"'>");
        out.write(thisDay+"<BR>"+dayCount+" pics</A></TD>");
        i++;
      }
    }

    current.add(step, 1);
  } // while

%>
   </TR></TABLE>

  </TD></TR>
 </FORM>
</TABLE>

<%=PicTemplate.footer(picSession)%>

